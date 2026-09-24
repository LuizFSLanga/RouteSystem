const routes=[
 {name:"Rota Centro — Tarde",driver:"João Silva",date:"21/09/2026",points:7,km:"84 km",stop:"1h 12min",status:"Em andamento"},
 {name:"Rota Norte — Manhã",driver:"Marcos Lima",date:"21/09/2026",points:9,km:"121 km",stop:"48 min",status:"Concluída"},
 {name:"Rota Pampulha",driver:"Rafael Souza",date:"21/09/2026",points:6,km:"73 km",stop:"36 min",status:"Em andamento"},
 {name:"Rota Barreiro",driver:"Ana Costa",date:"20/09/2026",points:11,km:"132 km",stop:"1h 51min",status:"Concluída"},
 {name:"Rota Venda Nova",driver:"Carlos Mendes",date:"20/09/2026",points:8,km:"96 km",stop:"54 min",status:"Concluída"}
];
const points=[
 ["Centro de Distribuição","Av. Amazonas, 4.520","08:15","09:57","1h 42min","Depósito"],
 ["Cliente A — Savassi","Rua Pernambuco, 1.200","10:22","11:20","58 min","Cliente"],
 ["Laboratório Norte","Av. Cristiano Machado, 820","12:05","12:49","44 min","Cliente"],
 ["Mercado Central","Av. Augusto de Lima, 744","14:02","14:33","31 min","Cliente"],
 ["Cliente B — Pampulha","Av. Antônio Carlos, 6.620","15:18","15:44","26 min","Cliente"],
 ["Coleta Industrial","Rua dos Tupis, 880","16:05","16:18","13 min","Coleta"]
];
const history=[
 ["21/09/2026","João Silva","Centro de Distribuição","08:15","09:57","1h 42min"],
 ["21/09/2026","Marcos Lima","Cliente A — Savassi","10:22","11:20","58 min"],
 ["21/09/2026","Rafael Souza","Laboratório Norte","12:05","12:49","44 min"],
 ["20/09/2026","Ana Costa","Mercado Central","14:02","14:33","31 min"],
 ["20/09/2026","Carlos Mendes","Cliente B — Pampulha","15:18","15:44","26 min"],
 ["20/09/2026","João Silva","Coleta Industrial","16:05","16:18","13 min"]
];
const drivers=[
 ["João Silva","JS","Rota Centro — Tarde","6h 42min","18 pontos"],
 ["Marcos Lima","ML","Rota Norte — Manhã","7h 15min","24 pontos"],
 ["Rafael Souza","RS","Rota Pampulha","5h 58min","15 pontos"],
 ["Ana Costa","AC","Rota Barreiro","7h 02min","22 pontos"],
 ["Carlos Mendes","CM","Rota Venda Nova","6h 31min","19 pontos"],
 ["Pedro Alves","PA","Disponível","—","—"]
];

const $=s=>document.querySelector(s), $$=s=>document.querySelectorAll(s);
function statusClass(s){return s==="Em andamento"?"green":s==="Pendente"?"orange":"gray"}
function renderRoutes(list=routes){
  $("#routesTable").innerHTML=list.map(r=>`<tr><td>${r.name}</td><td>${r.driver}</td><td>${r.date}</td><td>${r.points}</td><td>${r.km}</td><td>${r.stop}</td><td><span class="status ${statusClass(r.status)}">${r.status}</span></td><td>⋮</td></tr>`).join("");
  $("#dashboardRoutes").innerHTML=routes.slice(0,4).map(r=>`<tr><td>${r.driver}</td><td>${r.name}</td><td>${r.points}</td><td>${r.stop}</td><td><span class="status ${statusClass(r.status)}">${r.status}</span></td></tr>`).join("");
}
function renderPoints(list=points){$("#pointsTable").innerHTML=list.map(p=>`<tr><td>${p[0]}</td><td>${p[1]}</td><td>${p[2]}</td><td>${p[3]}</td><td><b>${p[4]}</b></td><td>${p[5]}</td></tr>`).join("")}
function renderHistory(){ $("#historyTable").innerHTML=history.map(h=>`<tr>${h.map((x,i)=>`<td class="${i===5?'strong':''}">${x}</td>`).join("")}</tr>`).join("")}
function renderDrivers(){ $("#driverGrid").innerHTML=drivers.map(d=>`<article class="driver-card"><div class="driver-head"><div class="driver-avatar">${d[1]}</div><div><strong>${d[0]}</strong><small>${d[2]}</small></div></div><div class="driver-stats"><div><span>Jornada</span><b>${d[3]}</b></div><div><span>Pontos atendidos</span><b>${d[4]}</b></div></div></article>`).join("")}
function renderChart(){
 const vals=[3.1,5.4,4.2,6.1,3.8,5.2,4.3], days=["Seg","Ter","Qua","Qui","Sex","Sáb","Dom"], max=7;
 $("#barChart").innerHTML=vals.map((v,i)=>`<div class="bar" style="height:${v/max*92}%"><span>${days[i]}</span></div>`).join("");
}
function navigate(page){
 $$(".page").forEach(p=>p.classList.toggle("active",p.id===page));
 $$(".nav-item").forEach(b=>b.classList.toggle("active",b.dataset.page===page));
 const names={dashboard:"Dashboard",rotas:"Rotas",pontos:"Pontos",motoristas:"Motoristas",historico:"Histórico",configuracoes:"Configurações"};
 $("#pageName").textContent=names[page]; $("#pageTitle").textContent=page==="dashboard"?"Visão geral":names[page];
 window.scrollTo({top:0,behavior:"smooth"});
}
$$(".nav-item").forEach(b=>b.addEventListener("click",()=>{navigate(b.dataset.page);$(".sidebar").classList.remove("mobile-open")}));
$$("[data-page-link]").forEach(b=>b.addEventListener("click",()=>navigate(b.dataset.pageLink)));
$("#mobileMenu").addEventListener("click",()=>$(".sidebar").classList.toggle("mobile-open"));

function openModal(id){$("#modalBackdrop").classList.add("open");$("#"+id).classList.add("open")}
function closeModal(){$("#modalBackdrop").classList.remove("open");$$(".modal").forEach(m=>m.classList.remove("open"))}
$$("[data-modal]").forEach(b=>b.addEventListener("click",()=>openModal(b.dataset.modal)));
$$(".close").forEach(b=>b.addEventListener("click",closeModal));
$("#modalBackdrop").addEventListener("click",e=>{if(e.target.id==="modalBackdrop")closeModal()});
$$(".modal-form").forEach(f=>f.addEventListener("submit",e=>{e.preventDefault();closeModal();showToast("Registro salvo com sucesso.");}));
function showToast(msg){$("#toast").textContent=msg;$("#toast").classList.add("show");setTimeout(()=>$("#toast").classList.remove("show"),2500)}

$("#routeSearch").addEventListener("input",e=>{const q=e.target.value.toLowerCase();renderRoutes(routes.filter(r=>(r.name+" "+r.driver).toLowerCase().includes(q)))});
$("#pointSearch").addEventListener("input",e=>{const q=e.target.value.toLowerCase();renderPoints(points.filter(p=>p.join(" ").toLowerCase().includes(q)))});
$("#saveConfig").addEventListener("click",()=>showToast("Configurações salvas com sucesso."));
$("#exportBtn").addEventListener("click",()=>{
 const csv=["Data,Motorista,Ponto,Chegada,Saída,Duração",...history.map(r=>r.map(x=>`"${x}"`).join(","))].join("\n");
 const blob=new Blob([csv],{type:"text/csv;charset=utf-8"}), a=document.createElement("a");a.href=URL.createObjectURL(blob);a.download="historico-paradas.csv";a.click();URL.revokeObjectURL(a.href);showToast("CSV exportado.");
});
renderRoutes();renderPoints();renderHistory();renderDrivers();renderChart();