// ================= Utilitários =================
const $ = s => document.querySelector(s);
const $$ = s => document.querySelectorAll(s);

// Estado em memória (fonte única dos dados exibidos)
const S = { motoristas: [], gerentes: [], roteiros: [], pontos: [], param: null };

// RN04: jornada padrão (8h por padrão; editável em Configurações)
const jornadaHoras = () => parseFloat(String((S.param && S.param.jornadaPadraoHoras) || "8").replace(",", ".")) || 8;
const jornadaMin = () => jornadaHoras() * 60;
const turnoDe = d => { const h = d.getHours(); return h < 12 ? "manha" : h < 18 ? "tarde" : "noite"; };

const esc = v => String(v ?? "").replace(/[&<>"']/g, c =>
  ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
const pad = n => String(n).padStart(2, "0");
const brl = n => "R$ " + (Number(n) || 0).toLocaleString("pt-BR", { minimumFractionDigits: 2, maximumFractionDigits: 2 });

function fmtMin(m) {
  if (m == null || isNaN(m)) return "—";
  const h = Math.floor(m / 60), r = Math.round(m % 60);
  return h ? `${h}h ${pad(r)}min` : `${r}min`;
}
function toDate(v) {
  if (!v) return null;
  if (typeof v === "string" && /^\d{4}-\d{2}-\d{2}$/.test(v)) v += "T00:00:00"; // evita bug de fuso
  const d = new Date(v);
  return isNaN(d) ? null : d;
}
const fmtData = v => { const d = toDate(v); return d ? d.toLocaleDateString("pt-BR") : "—"; };
const fmtHora = v => { const d = toDate(v); return d ? d.toLocaleString("pt-BR", { day: "2-digit", month: "2-digit", hour: "2-digit", minute: "2-digit" }) : "—"; };
const diaKey = d => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
const hojeKey = () => diaKey(new Date());

function statusClass(s) {
  const str = (s || "").toLowerCase();
  if (str.includes("andamento")) return "green";
  if (str.includes("pendente")) return "orange";
  return "gray";
}
function showToast(msg) {
  const t = $("#toast");
  t.textContent = msg;
  t.classList.add("show");
  setTimeout(() => t.classList.remove("show"), 2800);
}

// Chamada à API: lança Error com a mensagem do servidor quando falha
async function api(url, opts) {
  const res = await fetch(url, opts);
  let body = null;
  try { body = await res.json(); } catch (e) { /* resposta sem JSON */ }
  if (!res.ok) throw new Error((body && body.mensagem) || `Erro ${res.status} em ${url}`);
  return body;
}
const post = (url, obj) => api(url, {
  method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(obj)
});
const del = url => api(url, { method: "DELETE" });

// ================= Regras de negócio no front =================
// RN01/RN03: o ponto de partida (ordem 1) não conta tempo parado
const pontosParados = () => S.pontos.filter(p => p.ordemNoRoteiro > 1);
const minutosPonto = p => (p.ordemNoRoteiro > 1 ? Number(p.tempoParadoCalculado) || 0 : 0);
const nomeMotorista = id => (S.motoristas.find(m => m.id === id) || {}).nome || `#${id}`;
const roteiroDoPonto = p => S.roteiros.find(r => r.id === p.idRoteiro);

function statusRoteiro(r) {
  if (!S.pontos.some(p => p.idRoteiro === r.id)) return "Pendente";
  return r.distanciaTotal > 0 ? "Concluída" : "Em andamento";
}
// Data "de referência" de um ponto: chegada, ou data do roteiro
function dataPonto(p) {
  const r = roteiroDoPonto(p);
  return toDate(p.horarioChegada) || (r && toDate(r.data));
}

// ================= Carregamento =================
async function carregar(url, alvo) {
  try { S[alvo] = (await api(url)) || (alvo === "param" ? null : []); }
  catch (e) { console.warn(e.message); if (alvo !== "param") S[alvo] = []; }
}
async function recarregarTudo() {
  await Promise.all([
    carregar("/api/motoristas", "motoristas"),
    carregar("/api/gerentes", "gerentes"),
    carregar("/api/roteiros", "roteiros"),
    carregar("/api/pontos", "pontos"),   // endpoint a criar no backend (passo 3)
    carregar("/api/parametros", "param")
  ]);
  renderTudo();
}

// ================= Renderização =================
function renderTudo() {
  atualizarSelects();
  renderDrivers();
  renderManagers();
  renderRoutes();
  renderPoints();
  renderKpis();
  renderChart();
  renderRanking();
  renderHistory();
  renderConfig();
}

function atualizarSelects() {
  $$("select[name=idMotorista]").forEach(sel => {
    const atual = sel.value;
    sel.innerHTML = S.motoristas.map(m => `<option value="${m.id}">${esc(m.nome)}</option>`).join("");
    if (atual) sel.value = atual;
  });
  const hm = $("#histMotorista");
  if (hm) {
    const atual = hm.value;
    hm.innerHTML = `<option value="">Todos os motoristas</option>` +
      S.motoristas.map(m => `<option value="${m.id}">${esc(m.nome)}</option>`).join("");
    hm.value = atual;
  }
  $$("select[name=idRoteiro]").forEach(sel => {
    const atual = sel.value;
    sel.innerHTML = S.roteiros.map(r =>
      `<option value="${r.id}">Roteiro #${r.id} — ${esc(nomeMotorista(r.idMotorista))} (${fmtData(r.data)})</option>`).join("");
    if (atual) sel.value = atual;
  });
}

function renderDrivers() {
  const c = $("#driverGrid");
  if (!S.motoristas.length) { c.innerHTML = `<p class="muted">Nenhum motorista cadastrado.</p>`; return; }
  c.innerHTML = S.motoristas.map(m => {
    const ini = (m.nome || "M").split(" ").map(n => n[0]).join("").substring(0, 2).toUpperCase();
    const nRot = S.roteiros.filter(r => r.idMotorista === m.id).length;
    return `
      <article class="driver-card">
        <div class="driver-head">
          <div class="driver-avatar">${esc(ini)}</div>
          <div><strong>${esc(m.nome)}</strong><small>Doc.: ${esc(m.documento || "Não informado")}</small></div>
        </div>
        <div class="driver-stats">
          <div><span>Veículo</span><b>${esc(m.veiculo || "—")}</b></div>
          <div><span>Rendimento</span><b>${m.rendimentoKmLitro ? esc(m.rendimentoKmLitro) + " km/L" : "Padrão"}</b></div>
          <div><span>Telefone</span><b>${esc(m.telefone || "—")}</b></div>
          <div><span>Roteiros</span><b>${nRot}</b></div>
        </div>
      </article>`;
  }).join("");
}

function renderManagers() {
  const tb = $("#managersTable");
  if (!tb) return;
  tb.innerHTML = S.gerentes.length ? S.gerentes.map(g => `
    <tr>
      <td>${esc(g.nome)}</td>
      <td>${esc(g.telefone || "—")}</td>
      <td>${esc(g.email || "—")}</td>
      <td>${esc(g.equipeSobResponsabilidade || "—")}</td>
      <td><button class="link-btn danger" onclick="excluirGerente(${Number(g.id)})">Excluir</button></td>
    </tr>`).join("") : `<tr><td colspan="5">Nenhum gerente cadastrado.</td></tr>`;
}

async function excluirGerente(id) {
  if (!confirm("Excluir este gerente?")) return;
  try {
    const r = await del(`/api/gerentes/${id}`);
    showToast((r && r.mensagem) || "Gerente excluído.");
    await recarregarTudo();
  } catch (err) { console.error(err); showToast(err.message); }
}

function linhaRoteiro(r) {
  const st = statusRoteiro(r);
  const nPontos = S.pontos.filter(p => p.idRoteiro === r.id).length;
  return `
    <tr>
      <td>Roteiro #${r.id}</td>
      <td>${esc(nomeMotorista(r.idMotorista))}</td>
      <td>${fmtData(r.data)}</td>
      <td>${nPontos}</td>
      <td>${r.distanciaTotal ? Number(r.distanciaTotal).toFixed(1) + " km" : "0 km"}</td>
      <td>${typeof r.tempoTotalParado === "number" ? fmtMin(r.tempoTotalParado) : "—"}</td>
      <td><span class="status ${statusClass(st)}">${st}</span></td>
      <td><button class="link-btn" onclick="calcularRoteiro(${r.id})">Calcular</button></td>
    </tr>`;
}

function renderRoutes() {
  const busca = ($("#routeSearch").value || "").toLowerCase();
  const filtros = $$("#rotas .filter-row select, #rotas .filter-row input[type=date]");
  const statusSel = filtros[0] ? filtros[0].value : "Todos os status";
  const dataSel = filtros[1] ? filtros[1].value : "";

  const lista = S.roteiros.filter(r => {
    if (busca && !(`roteiro #${r.id} ${nomeMotorista(r.idMotorista)}`).toLowerCase().includes(busca)) return false;
    if (statusSel !== "Todos os status" && statusRoteiro(r) !== statusSel) return false;
    if (dataSel) { const d = toDate(r.data); if (!d || diaKey(d) !== dataSel) return false; }
    return true;
  });
  $("#routesTable").innerHTML = lista.length
    ? lista.map(linhaRoteiro).join("")
    : `<tr><td colspan="8">Nenhum roteiro encontrado.</td></tr>`;

  const andamento = S.roteiros.filter(r => statusRoteiro(r) === "Em andamento");
  $("#dashboardRoutes").innerHTML = andamento.length
    ? andamento.map(linhaRoteiro).join("")
    : `<tr><td colspan="8">Nenhuma rota em andamento.</td></tr>`;
}

function renderPoints() {
  const busca = ($("#pointSearch").value || "").toLowerCase();
  const tipo = ($("#pointType") || {}).value || "";
  const lista = S.pontos.filter(p => {
    if (busca && !(`${p.endereco} ${p.id}`).toLowerCase().includes(busca)) return false;
    if (tipo && (p.ordemNoRoteiro === 1 ? "Partida" : "Parada") !== tipo) return false;
    return true;
  });
  $("#pointsTable").innerHTML = lista.length ? lista.map(p => `
    <tr>
      <td>#${p.id} · ${p.ordemNoRoteiro}º do roteiro ${p.idRoteiro}</td>
      <td>${esc(p.endereco)}</td>
      <td>${fmtHora(p.horarioChegada)}</td>
      <td>${fmtHora(p.horarioSaida)}</td>
      <td>${p.ordemNoRoteiro > 1 ? fmtMin(p.tempoParadoCalculado) : "— (partida)"}</td>
      <td>${p.ordemNoRoteiro === 1 ? "Partida" : "Parada"}</td>
    </tr>`).join("") : `<tr><td colspan="6">Nenhum ponto cadastrado.</td></tr>`;

  const strips = $$(".summary-strip > div");
  const hoje = pontosParados().filter(p => { const d = dataPonto(p); return d && diaKey(d) === hojeKey(); });
  const maior = Math.max(0, ...hoje.map(minutosPonto));
  if (strips.length >= 3) {
    strips[0].querySelector("strong").textContent = S.pontos.length;
    strips[1].querySelector("strong").textContent = new Set(S.pontos.map(p => p.endereco)).size;
    strips[1].querySelector("span").textContent = "endereços distintos";
    strips[2].querySelector("strong").textContent = fmtMin(maior);
  }
}

function renderKpis() {
  const strongs = $$(".kpi-card strong"), smalls = $$(".kpi-card small");
  if (strongs.length < 4) return;
  const hoje = pontosParados().filter(p => { const d = dataPonto(p); return d && diaKey(d) === hojeKey(); });
  const minHoje = hoje.reduce((s, p) => s + minutosPonto(p), 0);
  const roteirosHoje = new Set(hoje.map(p => p.idRoteiro)).size;
  const dist = S.roteiros.reduce((s, r) => s + (Number(r.distanciaTotal) || 0), 0);
  const custo = S.roteiros.reduce((s, r) => s + (Number(r.custoEstimado) || 0), 0);

  strongs[0].textContent = fmtMin(minHoje);
  smalls[0].textContent = roteirosHoje
    ? `${Math.round(minHoje / (roteirosHoje * jornadaMin()) * 100)}% da jornada de ${jornadaHoras()}h` // RN04
    : "sem paradas hoje";
  strongs[1].textContent = pontosParados().length;
  smalls[1].textContent = `${S.motoristas.length} motoristas`;
  strongs[2].textContent = dist.toFixed(0) + " km";
  smalls[2].textContent = `${S.roteiros.filter(r => statusRoteiro(r) === "Concluída").length} rotas concluídas`;
  strongs[3].textContent = brl(custo);
  smalls[3].textContent = dist ? brl(custo / dist) + " / km" : "—";
}

// Gráfico: tempo parado por dia (RF08). Período vem do select #chartPeriod
function renderChart() {
  const barChart = $("#barChart");
  const periodo = $("#chartPeriod").value;
  const hoje = new Date();
  if (periodo === "12 meses") { renderChartMensal(barChart, hoje); return; }
  let dias = [];
  if (periodo === "Este mês") {
    for (let d = 1; d <= hoje.getDate(); d++) dias.push(new Date(hoje.getFullYear(), hoje.getMonth(), d));
  } else {
    const n = periodo === "30 dias" ? 30 : 7;
    for (let i = n - 1; i >= 0; i--) dias.push(new Date(hoje.getFullYear(), hoje.getMonth(), hoje.getDate() - i));
  }
  const porDia = {};
  pontosParados().forEach(p => {
    const d = dataPonto(p);
    if (d) porDia[diaKey(d)] = (porDia[diaKey(d)] || 0) + minutosPonto(p);
  });
  const vals = dias.map(d => (porDia[diaKey(d)] || 0) / 60);
  const max = Math.max(...vals, 0.5);
  const passo = dias.length > 10 ? 5 : 1;
  const semana = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"];
  barChart.innerHTML = vals.map((v, i) => {
    const rot = dias.length > 10 ? pad(dias[i].getDate()) : semana[dias[i].getDay()];
    return `<div class="bar" title="${fmtMin(v * 60)}" style="height:${Math.max(v / max * 92, v ? 2 : 0)}%">
              <span>${i % passo === 0 ? rot : ""}</span></div>`;
  }).join("");
  const sub = barChart.closest(".chart-card").querySelector(".card-head p");
  if (sub) sub.textContent = periodo === "Este mês" ? "Mês atual" : `Últimos ${periodo}`;
}

// RF08 "por mês": tempo parado somado por mês nos últimos 12 meses
function renderChartMensal(barChart, hoje) {
  const meses = [];
  for (let i = 11; i >= 0; i--) meses.push(new Date(hoje.getFullYear(), hoje.getMonth() - i, 1));
  const chave = d => `${d.getFullYear()}-${pad(d.getMonth() + 1)}`;
  const porMes = {};
  pontosParados().forEach(p => {
    const d = dataPonto(p);
    if (d) porMes[chave(d)] = (porMes[chave(d)] || 0) + minutosPonto(p);
  });
  const vals = meses.map(m => (porMes[chave(m)] || 0) / 60);
  const max = Math.max(...vals, 0.5);
  barChart.innerHTML = vals.map((v, i) => `
    <div class="bar" title="${pad(meses[i].getMonth() + 1)}/${meses[i].getFullYear()}: ${fmtMin(v * 60)}" style="height:${Math.max(v / max * 92, v ? 2 : 0)}%">
      <span>${pad(meses[i].getMonth() + 1)}/${String(meses[i].getFullYear()).slice(2)}</span></div>`).join("");
  const sub = barChart.closest(".chart-card").querySelector(".card-head p");
  if (sub) sub.textContent = "Últimos 12 meses (por mês)";
}

function renderRanking() {
  const top = [...pontosParados()].sort((a, b) => minutosPonto(b) - minutosPonto(a)).slice(0, 4);
  $(".ranking").innerHTML = top.length ? top.map((p, i) => `
    <div class="rank-row"><b>${pad(i + 1)}</b><div><small>${esc(p.endereco)}</small></div><em>${fmtMin(minutosPonto(p))}</em></div>
  `).join("") : `<p class="muted">Sem dados de paradas ainda.</p>`;
}

// Histórico (RF07): filtra por intervalo de datas
function pontosFiltradosHistorico() {
  const [ini, fim] = [...$$("#historico input[type=date]")].map(i => i.value);
  const idMot = parseInt(($("#histMotorista") || {}).value) || 0;
  const turno = ($("#histTurno") || {}).value || "";
  return S.pontos.filter(p => {
    const d = dataPonto(p);
    if (!d) return false;
    const k = diaKey(d);
    if ((ini && k < ini) || (fim && k > fim)) return false;
    if (idMot) { const r = roteiroDoPonto(p); if (!r || r.idMotorista !== idMot) return false; }
    if (turno && turnoDe(toDate(p.horarioChegada) || d) !== turno) return false;
    return true;
  });
}
function renderHistory() {
  const lista = pontosFiltradosHistorico();
  $("#historyTable").innerHTML = lista.length ? lista.map(p => {
    const r = roteiroDoPonto(p);
    return `<tr>
      <td>${fmtData(dataPonto(p))}</td>
      <td>${esc(r ? nomeMotorista(r.idMotorista) : "—")}</td>
      <td>${esc(p.endereco)}</td>
      <td>${fmtHora(p.horarioChegada)}</td>
      <td>${fmtHora(p.horarioSaida)}</td>
      <td>${p.ordemNoRoteiro > 1 ? fmtMin(p.tempoParadoCalculado) : "—"}</td>
    </tr>`;
  }).join("") : `<tr><td colspan="6">Nenhum registro no período.</td></tr>`;

  // Donut por período do dia (manhã < 12h, tarde 12–18h, noite)
  const per = [0, 0, 0];
  lista.filter(p => p.ordemNoRoteiro > 1).forEach(p => {
    const t = turnoDe(toDate(p.horarioChegada) || new Date(0));
    per[t === "manha" ? 0 : t === "tarde" ? 1 : 2] += minutosPonto(p);
  });
  const total = per[0] + per[1] + per[2];
  const pct = per.map(v => total ? Math.round(v / total * 100) : 0);
  const donut = $(".donut");
  donut.style.background = total
    ? `conic-gradient(#617bf1 0 ${pct[0]}%,#72bf91 ${pct[0]}% ${pct[0] + pct[1]}%,#f2ae61 ${pct[0] + pct[1]}% 100%)`
    : "#eef1f5";
  donut.querySelector("strong").textContent = fmtMin(total);
  $$(".donut-legend b").forEach((b, i) => b.textContent = pct[i] + "%");

  // Custos (RF11)
  const boxes = $$("#historico .cost-box > div");
  if (boxes.length >= 4 && S.param) {
    const dist = S.roteiros.reduce((s, r) => s + (Number(r.distanciaTotal) || 0), 0);
    const custo = S.roteiros.reduce((s, r) => s + (Number(r.custoEstimado) || 0), 0);
    const comb = S.param.kmLitro ? dist / S.param.kmLitro * S.param.valorCombustivel : 0;
    boxes[0].querySelector("strong").textContent = brl(S.param.custoPorKm);
    boxes[1].querySelector("strong").textContent = (S.param.kmLitro || 0) + " km/L";
    boxes[2].querySelector("strong").textContent = brl(comb);
    boxes[3].querySelector("span").textContent = "Custo total estimado";
    boxes[3].querySelector("strong").textContent = brl(custo);
  }
}

function renderConfig() {
  if (!S.param) return;
  $("#cfgCombustivel").value = S.param.valorCombustivel ?? 6.29;
  $("#cfgCustoKm").value = S.param.custoPorKm ?? 1.53;
  $("#cfgKmLitro").value = S.param.kmLitro ?? 9.8;
  $("#cfgJornada").value = jornadaHoras();
  if (S.param.regrasDeCalculo) $("#cfgRegras").value = S.param.regrasDeCalculo;
}

// ================= Exportar CSV (RF12) =================
function exportarCsv() {
  const lista = pontosFiltradosHistorico();
  if (!lista.length) { showToast("Nada para exportar no período."); return; }
  const cel = v => `"${String(v ?? "").replace(/"/g, '""')}"`;
  const linhas = [["Data", "Motorista", "Endereço", "Chegada", "Saída", "Minutos parado"].map(cel).join(";")];
  lista.forEach(p => {
    const r = roteiroDoPonto(p);
    linhas.push([fmtData(dataPonto(p)), r ? nomeMotorista(r.idMotorista) : "", p.endereco,
      fmtHora(p.horarioChegada), fmtHora(p.horarioSaida), minutosPonto(p)].map(cel).join(";"));
  });
  const blob = new Blob(["\ufeff" + linhas.join("\r\n")], { type: "text/csv;charset=utf-8" });
  const a = document.createElement("a");
  a.href = URL.createObjectURL(blob);
  a.download = `historico_${hojeKey()}.csv`;
  a.click();
  URL.revokeObjectURL(a.href);
}

// ================= Formulários (campos montados aqui, sem mexer no HTML) =================
const campo = (rotulo, nome, attrs = "") => `<label>${rotulo}<input name="${nome}" ${attrs}></label>`;
const seletor = (rotulo, nome) => `<label>${rotulo}<select name="${nome}" required></select></label>`;

function montarFormularios() {
  const hoje = hojeKey();
  const ponto = (btn) => `
    ${seletor("Roteiro", "idRoteiro")}
    ${campo("Endereço", "endereco", 'required placeholder="Rua, número, bairro"')}
    <div class="two">
      ${campo("Latitude", "latitude", 'type="number" step="any" required placeholder="-19.9167"')}
      ${campo("Longitude", "longitude", 'type="number" step="any" required placeholder="-43.9345"')}
    </div>
    ${campo("Ordem no roteiro (vazio = próxima)", "ordemNoRoteiro", 'type="number" min="1"')}
    <div class="two">
      ${campo("Chegada", "chegada", 'type="datetime-local" required')}
      ${campo("Saída", "saida", 'type="datetime-local" required')}
    </div>
    <button class="primary" type="submit">${btn}</button>`;

  $("#modalMotorista .modal-form").innerHTML = `
    ${campo("Nome completo", "nome", "required")}
    ${campo("Documento (CNH / CPF)", "documento", "required")}
    ${campo("Telefone", "telefone")}
    ${campo("Veículo", "veiculo")}
    ${campo("Rendimento (km/L)", "rendimentoKmLitro", 'type="number" step="0.1" min="0.1" required')}
    <button class="primary" type="submit">Cadastrar</button>`;
  $("#modalGerente .modal-form").innerHTML = `
    ${campo("Nome completo", "nome", "required")}
    ${campo("Telefone", "telefone", 'type="tel"')}
    ${campo("E-mail", "email", 'type="email" required')}
    ${campo("Equipe sob responsabilidade", "equipeSobResponsabilidade", 'placeholder="Ex.: Entregas Zona Norte"')}
    <button class="primary" type="submit">Cadastrar</button>`;
  $("#modalRota .modal-form").innerHTML = `
    ${seletor("Motorista", "idMotorista")}
    ${campo("Data", "data", `type="date" required value="${hoje}"`)}
    <button class="primary" type="submit">Criar rota</button>`;
  $("#modalPonto .modal-form").innerHTML = ponto("Cadastrar ponto");
  $("#modalRegistro .modal-form").innerHTML = ponto("Registrar parada");
}

const valor = (form, nome) => form.elements[nome].value.trim();
const isoLocal = v => (v.length === 16 ? v + ":00" : v); // datetime-local -> yyyy-MM-ddTHH:mm:ss

function ligarFormulario(sel, montar, url, msgOk) {
  const form = $(sel + " form");
  form.addEventListener("submit", async e => {
    e.preventDefault();
    try {
      const dados = montar(form);
      if (!dados) return;
      await post(url, dados);
      showToast(msgOk);
      closeModal();
      form.reset();
      await recarregarTudo();
    } catch (err) {
      console.error(err);
      showToast(err.message || "Erro de conexão com o servidor.");
    }
  });
}

function ligarFormularios() {
  ligarFormulario("#modalMotorista", f => ({
    nome: valor(f, "nome"),
    documento: valor(f, "documento"),
    telefone: valor(f, "telefone"),
    veiculo: valor(f, "veiculo"),
    rendimentoKmLitro: parseFloat(valor(f, "rendimentoKmLitro"))
  }), "/api/motoristas", "Motorista cadastrado com sucesso!");

  ligarFormulario("#modalGerente", f => ({
    nome: valor(f, "nome"),
    telefone: valor(f, "telefone"),
    email: valor(f, "email"),
    equipeSobResponsabilidade: valor(f, "equipeSobResponsabilidade")
  }), "/api/gerentes", "Gerente cadastrado com sucesso!");

  ligarFormulario("#modalRota", f => ({
    idMotorista: parseInt(valor(f, "idMotorista")),
    data: valor(f, "data") + "T00:00:00",
    distanciaTotal: 0,
    custoEstimado: 0
  }), "/api/roteiros", "Rota criada com sucesso!");

  const montarPonto = f => {
    const idRoteiro = parseInt(valor(f, "idRoteiro"));
    const chegada = valor(f, "chegada"), saida = valor(f, "saida");
    if (saida < chegada) { showToast("A saída não pode ser antes da chegada."); return null; }
    const doRoteiro = S.pontos.filter(p => p.idRoteiro === idRoteiro);
    const ordem = parseInt(valor(f, "ordemNoRoteiro")) ||
      (Math.max(0, ...doRoteiro.map(p => p.ordemNoRoteiro)) + 1);
    if (doRoteiro.some(p => p.ordemNoRoteiro === ordem)) { showToast(`Já existe o ponto ${ordem} neste roteiro.`); return null; }
    const minutos = Math.round((new Date(saida) - new Date(chegada)) / 60000);
    return {
      idRoteiro, endereco: valor(f, "endereco"),
      latitude: parseFloat(valor(f, "latitude")), longitude: parseFloat(valor(f, "longitude")),
      ordemNoRoteiro: ordem,
      horarioChegada: isoLocal(chegada), horarioSaida: isoLocal(saida),
      tempoParadoCalculado: ordem > 1 ? minutos : 0 // RN01: partida não conta
    };
  };
  ligarFormulario("#modalPonto", montarPonto, "/api/pontos", "Ponto cadastrado!");
  ligarFormulario("#modalRegistro", montarPonto, "/api/pontos", "Parada registrada!");
}

// Configurações (RF09/RF10)
function ligarConfig() {
  $("#saveConfig").addEventListener("click", async () => {
    const p = {
      id: S.param ? S.param.id : 0,
      valorCombustivel: parseFloat($("#cfgCombustivel").value),
      custoPorKm: parseFloat($("#cfgCustoKm").value),
      kmLitro: parseFloat($("#cfgKmLitro").value),
      jornadaPadraoHoras: String($("#cfgJornada").value || "8"),   // RF10 / RN04
      regrasDeCalculo: $("#cfgRegras").value.trim()                // RF10
    };
    if ([p.valorCombustivel, p.custoPorKm, p.kmLitro].some(isNaN)) { showToast("Preencha todos os valores numéricos."); return; }
    try {
      await post("/api/parametros", p);
      showToast("Parâmetros do sistema salvos!");
      await recarregarTudo();
    } catch (err) { showToast(err.message); }
  });
}

async function calcularRoteiro(id) {
  try {
    const data = await post(`/api/roteiros/${id}/calcular`, {});
    showToast(data.mensagem || "Roteiro recalculado!");
    await recarregarTudo();
  } catch (err) { console.error(err); showToast(err.message); }
}

// ================= Navegação e modais =================
function navigate(page) {
  $$(".page").forEach(p => p.classList.toggle("active", p.id === page));
  $$(".nav-item").forEach(b => b.classList.toggle("active", b.dataset.page === page));
  const nomes = { dashboard: "Dashboard", rotas: "Rotas", pontos: "Pontos", motoristas: "Motoristas", gerentes: "Gerentes", historico: "Histórico", configuracoes: "Configurações" };
  $("#pageName").textContent = nomes[page];
  $("#pageTitle").textContent = page === "dashboard" ? "Visão geral" : nomes[page];
  window.scrollTo({ top: 0, behavior: "smooth" });
}
function openModal(id) {
  $("#modalBackdrop").classList.add("open");
  $("#" + id).classList.add("open");
  atualizarSelects();
}
function closeModal() {
  $("#modalBackdrop").classList.remove("open");
  $$(".modal").forEach(m => m.classList.remove("open"));
}

function ligarEventos() {
  $$(".nav-item").forEach(b => b.addEventListener("click", () => {
    navigate(b.dataset.page);
    $(".sidebar").classList.remove("mobile-open");
  }));
  $$("[data-page-link]").forEach(b => b.addEventListener("click", () => navigate(b.dataset.pageLink)));
  $("#mobileMenu").addEventListener("click", () => $(".sidebar").classList.toggle("mobile-open"));
  $$("[data-modal]").forEach(b => b.addEventListener("click", () => openModal(b.dataset.modal)));
  $$(".close").forEach(b => b.addEventListener("click", closeModal));
  $("#modalBackdrop").addEventListener("click", e => { if (e.target.id === "modalBackdrop") closeModal(); });

  // Filtros e busca
  $("#chartPeriod").addEventListener("change", renderChart);
  $("#routeSearch").addEventListener("input", renderRoutes);
  $$("#rotas .filter-row select, #rotas .filter-row input[type=date]").forEach(el => el.addEventListener("change", renderRoutes));
  $("#pointSearch").addEventListener("input", renderPoints);
  $("#pointType").addEventListener("change", renderPoints);
  $("#histMotorista").addEventListener("change", renderHistory);
  $("#histTurno").addEventListener("change", renderHistory);
  $$("#historico input[type=date]").forEach(el => el.addEventListener("change", renderHistory));
  $("#exportBtn").addEventListener("click", exportarCsv);
}

window.addEventListener("DOMContentLoaded", () => {
  // Valores de exemplo do HTML não devem filtrar nada
  const dataRotas = $("#rotas input[type=date]");
  if (dataRotas) dataRotas.value = "";
  montarFormularios();
  ligarEventos();
  ligarFormularios();
  ligarConfig();
  recarregarTudo();
});