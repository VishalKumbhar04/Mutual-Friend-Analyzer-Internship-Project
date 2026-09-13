
public class UITemplate {

    public static String getHTML() {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title>Mutual Friend Analyzer</title>
<link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700;800&family=Fira+Code:wght@400;500&display=swap" rel="stylesheet"/>
<style>
:root{
  --bg:#08090d;--s0:#0e1017;--s1:#13161f;--s2:#191d28;
  --border:#232736;--border2:#2e3450;
  --gold:#f0c040;--teal:#2dd4bf;--rose:#fb7185;--blue:#60a5fa;
  --text:#e2e4ef;--muted:#52566e;--faint:#1e2130;
  --sans:'Space Grotesk',sans-serif;--mono:'Fira Code',monospace;
}
*,*::before,*::after{box-sizing:border-box;margin:0;padding:0}
html,body{height:100%}
body{font-family:var(--sans);background:var(--bg);color:var(--text);height:100vh;overflow:hidden;display:flex;flex-direction:column}
body::before{content:'';position:fixed;inset:0;pointer-events:none;z-index:0;
  background:radial-gradient(ellipse 70% 40% at 90% 0%,rgba(240,192,64,.06) 0%,transparent 60%),
             radial-gradient(ellipse 50% 60% at 0% 100%,rgba(45,212,191,.04) 0%,transparent 55%)}

/* HEADER */
.hdr{position:relative;z-index:10;background:var(--s0);border-bottom:1px solid var(--border);
  height:54px;padding:0 24px;display:flex;align-items:center;gap:14px;flex-shrink:0}
.logo{width:32px;height:32px;background:linear-gradient(135deg,var(--gold),var(--teal));
  border-radius:8px;display:flex;align-items:center;justify-content:center;font-size:15px;
  box-shadow:0 0 18px rgba(240,192,64,.25)}
.hdr-title{font-size:14px;font-weight:700}
.file-tags{margin-left:auto;display:flex;align-items:center;gap:7px}
.ftag{display:flex;align-items:center;gap:5px;font-family:var(--mono);font-size:9.5px;
  padding:4px 10px;border-radius:5px;border:1px solid var(--border);background:var(--s2);
  color:var(--muted);cursor:default;transition:all .2s;user-select:none}
.ftag:hover{border-color:var(--gold);color:var(--gold)}
.ftag .dot{width:5px;height:5px;border-radius:50%;flex-shrink:0}

/* LAYOUT */
.layout{position:relative;z-index:1;display:grid;grid-template-columns:320px 1fr;flex:1;min-height:0}

/* SIDEBAR */
.sb{background:var(--s0);border-right:1px solid var(--border);overflow-y:auto;display:flex;flex-direction:column}
.sb::-webkit-scrollbar{width:3px}
.sb::-webkit-scrollbar-thumb{background:var(--faint);border-radius:2px}
.sb-block{padding:18px 18px 0}
.sb-block:last-child{padding-bottom:18px;flex:1}
.divider{height:1px;background:var(--border);margin:16px 0 0}
.blk-hd{display:flex;align-items:center;justify-content:space-between;margin-bottom:11px}
.blk-title{font-size:9px;font-weight:700;letter-spacing:2.5px;text-transform:uppercase;color:var(--muted);font-family:var(--mono)}
.java-ref{font-family:var(--mono);font-size:8.5px;color:rgba(240,192,64,.5)}
.java-ref::before{content:'// ';color:var(--muted)}

.stats{display:grid;grid-template-columns:repeat(3,1fr);gap:6px}
.sbox{background:var(--s1);border:1px solid var(--border);border-radius:10px;padding:10px 8px;text-align:center}
.snum{font-size:19px;font-weight:800;display:block;line-height:1}
.slbl{font-family:var(--mono);font-size:8px;color:var(--muted);letter-spacing:1.5px;text-transform:uppercase;margin-top:3px;display:block}

input[type=text],select{width:100%;background:var(--bg);border:1px solid var(--border);border-radius:8px;
  padding:9px 12px;color:var(--text);font-family:var(--mono);font-size:12px;outline:none;transition:border-color .2s,box-shadow .2s}
input:focus,select:focus{border-color:var(--gold);box-shadow:0 0 0 3px rgba(240,192,64,.08)}
input::placeholder{color:var(--faint)}
select{cursor:pointer;appearance:none;
  background-image:url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='10' height='6'%3E%3Cpath fill='%2352566e' d='M5 6L0 0h10z'/%3E%3C/svg%3E");
  background-repeat:no-repeat;background-position:right 11px center}
select option{background:var(--s2)}
.irow{display:flex;gap:7px;margin-bottom:8px}
.irow input,.irow select{flex:1}
.brow{display:flex;align-items:center;justify-content:space-between;margin:7px 0 0}

.btn{font-family:var(--sans);font-weight:700;font-size:12px;border:none;border-radius:8px;
  cursor:pointer;padding:9px 16px;transition:all .18s;white-space:nowrap}
.bg{background:var(--gold);color:#08090d;box-shadow:0 3px 12px rgba(240,192,64,.25)}
.bg:hover{filter:brightness(1.1);box-shadow:0 4px 18px rgba(240,192,64,.4);transform:translateY(-1px)}
.bt{background:var(--teal);color:#08090d;box-shadow:0 3px 12px rgba(45,212,191,.2)}
.bt:hover{filter:brightness(1.1);transform:translateY(-1px)}
.bgh{background:var(--s2);color:var(--text);border:1px solid var(--border2)}
.bgh:hover{border-color:var(--gold);color:var(--gold)}
.br{background:rgba(251,113,133,.12);color:var(--rose);border:1px solid rgba(251,113,133,.25)}
.br:hover{background:rgba(251,113,133,.2)}
.bxs{padding:5px 10px;font-size:11px;border-radius:6px}
.tlbl{font-family:var(--mono);font-size:11px;color:var(--muted);display:flex;align-items:center;gap:6px;cursor:pointer}
input[type=checkbox]{accent-color:var(--gold);cursor:pointer}

.ulist{display:flex;flex-direction:column;gap:6px}
.uchip{display:flex;align-items:center;gap:8px;padding:8px 11px;background:var(--s1);
  border:1px solid var(--border);border-radius:8px;transition:all .18s;cursor:default}
.uchip:hover{border-color:var(--border2);background:var(--s2)}
.av{width:28px;height:28px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:12px;flex-shrink:0}
.cn{font-size:12px;font-weight:600;flex:1}
.cm{font-family:var(--mono);font-size:10px;color:var(--muted)}
.cacts{display:flex;gap:5px;opacity:0;transition:opacity .15s}
.uchip:hover .cacts{opacity:1}

/* MAIN */
.main{display:flex;flex-direction:column;overflow:hidden;background:var(--bg)}
.tabs{display:flex;background:var(--s0);border-bottom:1px solid var(--border);padding:0 22px;flex-shrink:0}
.tab{padding:16px 15px;font-size:12px;font-weight:700;letter-spacing:.3px;color:var(--muted);
  cursor:pointer;border-bottom:2px solid transparent;transition:all .2s}
.tab:hover{color:var(--text)}
.tab.active{color:var(--gold);border-bottom-color:var(--gold)}
.pane{display:none;flex:1;overflow:hidden}
.pane.active{display:flex;flex-direction:column}
.pscroll{flex:1;overflow-y:auto;padding:24px;display:flex;flex-direction:column;gap:18px}
.pscroll::-webkit-scrollbar{width:3px}
.pscroll::-webkit-scrollbar-thumb{background:var(--faint);border-radius:2px}

.method-hint{font-family:var(--mono);font-size:9px;color:var(--muted);letter-spacing:1.5px;
  text-transform:uppercase;margin-bottom:10px;display:flex;align-items:center;gap:8px}
.method-hint span{background:var(--s1);border:1px solid var(--border);padding:2px 8px;
  border-radius:4px;color:var(--gold);letter-spacing:0;text-transform:none;font-size:10px}
.crow{display:grid;grid-template-columns:1fr 32px 1fr;gap:10px;align-items:center}
.vs{width:32px;height:32px;background:var(--s2);border:1px solid var(--border2);border-radius:50%;
  display:flex;align-items:center;justify-content:center;font-family:var(--mono);font-size:9px;font-weight:700;color:var(--muted)}

.rcard{background:var(--s1);border:1px solid var(--border);border-radius:13px;padding:20px;animation:fadeUp .3s ease}
@keyframes fadeUp{from{opacity:0;transform:translateY(8px)}to{opacity:1;transform:translateY(0)}}
.rtop{display:flex;align-items:center;gap:9px;margin-bottom:14px;flex-wrap:wrap}
.rtitle{font-size:14px;font-weight:700}
.badge{font-family:var(--mono);font-size:10px;padding:3px 10px;border-radius:20px}
.b-gold{background:rgba(240,192,64,.12);color:var(--gold);border:1px solid rgba(240,192,64,.25)}
.b-teal{background:rgba(45,212,191,.1);color:var(--teal);border:1px solid rgba(45,212,191,.2)}
.b-muted{background:var(--faint);color:var(--muted);border:1px solid var(--border)}
.tags{display:flex;flex-wrap:wrap;gap:7px}
.tag{display:flex;align-items:center;gap:5px;padding:6px 12px;border-radius:40px;font-family:var(--mono);font-size:11px;animation:popIn .22s ease both}
@keyframes popIn{from{opacity:0;transform:scale(.65)}to{opacity:1;transform:scale(1)}}
.t-gold{background:rgba(240,192,64,.1);color:var(--gold);border:1px solid rgba(240,192,64,.2)}
.t-teal{background:rgba(45,212,191,.08);color:var(--teal);border:1px solid rgba(45,212,191,.18)}
.fgrid{display:grid;grid-template-columns:1fr 1fr;gap:14px;margin-top:14px}
.ftitle{font-family:var(--mono);font-size:9.5px;color:var(--muted);margin-bottom:7px}
.mtags{display:flex;flex-wrap:wrap;gap:4px}
.mtag{font-family:var(--mono);font-size:10px;padding:2px 8px;border-radius:20px}
.empty{text-align:center;padding:50px 20px;color:var(--muted)}
.ei{font-size:34px;margin-bottom:10px}
.empty p{font-family:var(--mono);font-size:11px;line-height:1.9}

.gwrap{flex:1;position:relative;overflow:hidden}
#gc{width:100%;height:100%;cursor:grab;display:block}
#gc:active{cursor:grabbing}
.gleg{position:absolute;bottom:18px;left:18px;background:rgba(14,16,23,.93);border:1px solid var(--border);
  border-radius:10px;padding:10px 13px;font-family:var(--mono);font-size:10.5px;color:var(--muted);
  display:flex;flex-direction:column;gap:6px;backdrop-filter:blur(6px)}
.li{display:flex;align-items:center;gap:7px}
.ld{width:9px;height:9px;border-radius:50%}
#matrix table{border-collapse:collapse}
#matrix td,#matrix th{border:1px solid var(--border);text-align:center;vertical-align:middle}

#toast{position:fixed;bottom:24px;right:24px;z-index:999;padding:11px 17px;border-radius:10px;
  font-family:var(--mono);font-size:11px;background:var(--s2);border:1px solid var(--border2);
  transform:translateX(130%);transition:transform .32s cubic-bezier(.34,1.56,.64,1);
  max-width:260px;box-shadow:0 8px 28px rgba(0,0,0,.5)}
#toast.show{transform:translateX(0)}
#toast.ok{border-color:var(--teal);color:var(--teal)}
#toast.er{border-color:var(--rose);color:var(--rose)}
#toast.in{border-color:var(--gold);color:var(--gold)}

.ov{position:fixed;inset:0;z-index:100;background:rgba(8,9,13,.85);backdrop-filter:blur(5px);
  display:none;align-items:center;justify-content:center}
.ov.open{display:flex}
.modal{background:var(--s1);border:1px solid var(--border2);border-radius:15px;padding:24px;
  width:400px;max-height:72vh;overflow-y:auto;animation:fadeUp .28s ease}
.mhd{display:flex;align-items:flex-start;justify-content:space-between;margin-bottom:16px}
.mtitle{font-size:16px;font-weight:800}
.msub{font-family:var(--mono);font-size:10px;color:var(--muted);margin-top:3px}
.mx{background:none;border:none;color:var(--muted);font-size:22px;cursor:pointer;line-height:1;transition:color .15s}
.mx:hover{color:var(--text)}
.mfrow{display:flex;align-items:center;justify-content:space-between;padding:8px 12px;
  background:var(--bg);border:1px solid var(--border);border-radius:7px;
  font-family:var(--mono);font-size:11px;margin-bottom:6px}
</style>
</head>
<body>

<header class="hdr">
  <div class="logo">🔗</div>
  <span class="hdr-title">Mutual Friend Analyzer</span>
  <div class="file-tags">
    <div class="ftag" title="HTTP server + all /api/* routes">
      <div class="dot" style="background:#f0c040"></div>Main.java
    </div>
    <div class="ftag" title="getMutualFriends() + suggestFriends()">
      <div class="dot" style="background:#2dd4bf"></div>UserService.java
    </div>
    <div class="ftag" title="saveData() + loadData() → data.ser">
      <div class="dot" style="background:#60a5fa"></div>FileService.java
    </div>
    <button class="btn bgh bxs" onclick="loadDemo()">Load Demo</button>
  </div>
</header>

<div class="layout">
  <aside class="sb">

    <div class="sb-block" style="padding-top:16px">
      <div class="blk-hd">
        <span class="blk-title">Network</span>
        <span class="java-ref">Main.java · userFriendsMap</span>
      </div>
      <div class="stats">
        <div class="sbox"><span class="snum" id="sU" style="color:var(--gold)">0</span><span class="slbl">Users</span></div>
        <div class="sbox"><span class="snum" id="sC" style="color:var(--teal)">0</span><span class="slbl">Links</span></div>
        <div class="sbox"><span class="snum" id="sD" style="color:var(--blue)">0%</span><span class="slbl">Density</span></div>
      </div>
    </div>
    <div class="divider"></div>

    <div class="sb-block">
      <div class="blk-hd">
        <span class="blk-title">Add User</span>
        <span class="java-ref">Main.java · addUser()</span>
      </div>
      <div class="irow">
        <input type="text" id="nU" placeholder="username…" onkeydown="if(event.key==='Enter')addUser()"/>
        <button class="btn bg" onclick="addUser()">Add</button>
      </div>
    </div>
    <div class="divider"></div>

    <div class="sb-block">
      <div class="blk-hd">
        <span class="blk-title">Add Friend</span>
        <span class="java-ref">Main.java · addFriend()</span>
      </div>
      <select id="f1" style="margin-bottom:7px"><option value="">Select user…</option></select>
      <select id="f2" style="margin-bottom:4px"><option value="">Select friend…</option></select>
      <div class="brow">
        <label class="tlbl"><input type="checkbox" id="bidi" checked/> Bidirectional</label>
        <button class="btn bt bxs" onclick="addFriend()">Connect</button>
      </div>
    </div>
    <div class="divider"></div>

    <div class="sb-block">
      <div class="blk-hd">
        <span class="blk-title">All Users</span>
        <div style="display:flex;gap:6px;align-items:center">
          <span class="java-ref">FileService.java · loadData()</span>
          <button class="btn br bxs" onclick="clearAll()">Clear</button>
        </div>
      </div>
      <div class="ulist" id="ulist">
        <div class="empty"><div class="ei">👤</div><p>No users yet.<br/>Add one above.</p></div>
      </div>
    </div>
  </aside>

  <main class="main">
    <div class="tabs">
      <div class="tab active" onclick="switchTab('mutual')">🔍 Mutual Friends</div>
      <div class="tab" onclick="switchTab('suggest')">💡 Suggestions</div>
      <div class="tab" onclick="switchTab('graph')">🕸️ Graph</div>
      <div class="tab" onclick="switchTab('matrix')">📊 Matrix</div>
    </div>

    <div class="pane active" id="p-mutual">
      <div class="pscroll">
        <div>
          <div class="method-hint">UserService.java <span>getMutualFriends(user1, user2, map)</span> ← Main.java</div>
          <div class="crow">
            <select id="m1" onchange="runMutual()"><option value="">Choose user 1…</option></select>
            <div class="vs">VS</div>
            <select id="m2" onchange="runMutual()"><option value="">Choose user 2…</option></select>
          </div>
        </div>
        <div id="mres"></div>
      </div>
    </div>

    <div class="pane" id="p-suggest">
      <div class="pscroll">
        <div>
          <div class="method-hint">UserService.java <span>suggestFriends(user1, user2, map)</span></div>
          <div class="crow">
            <select id="s1" onchange="runSuggest()"><option value="">Choose user 1…</option></select>
            <div class="vs">↔</div>
            <select id="s2" onchange="runSuggest()"><option value="">Choose user 2…</option></select>
          </div>
        </div>
        <div id="sres"></div>
      </div>
    </div>

    <div class="pane" id="p-graph">
      <div class="gwrap">
        <canvas id="gc"></canvas>
        <div class="gleg">
          <div class="li"><div class="ld" style="background:var(--gold)"></div> User Node</div>
          <div class="li"><div style="width:22px;height:2px;background:var(--gold);opacity:.4;border-radius:1px"></div> Connection</div>
          <div class="li" style="font-size:9.5px">Drag to rearrange</div>
        </div>
      </div>
    </div>

    <div class="pane" id="p-matrix">
      <div class="pscroll" id="matrix"></div>
    </div>
  </main>
</div>

<div id="toast"></div>
<div class="ov" id="ov" onclick="if(event.target===this)closeModal()">
  <div class="modal">
    <div class="mhd">
      <div><div class="mtitle" id="mt"></div><div class="msub" id="ms"></div></div>
      <button class="mx" onclick="closeModal()">×</button>
    </div>
    <div id="mb"></div>
  </div>
</div>

<script>
const API='http://localhost:8080';
let D={};

async function load(){
  try{const r=await fetch(API+'/api/users');const d=await r.json();D=d.users||{};}catch(e){}
  render();
}

async function addUser(){
  const inp=document.getElementById('nU');
  const username=inp.value.trim().toLowerCase();
  if(!username)return toast('Enter a username','er');
  try{
    const r=await fetch(API+'/api/user/add',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({username})});
    const d=await r.json();
    if(d.success){D[username]=[];inp.value='';render();toast(d.message,'ok');}
    else toast(d.message,'er');
  }catch(e){toast('Server error','er');}
}

async function addFriend(){
  const user=document.getElementById('f1').value;
  const friend=document.getElementById('f2').value;
  const bidi=document.getElementById('bidi').checked;
  if(!user||!friend)return toast('Select both users','er');
  if(user===friend)return toast('Cannot connect to self','er');
  try{
    const r=await fetch(API+'/api/friend/add',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({user,friend,bidirectional:String(bidi)})});
    const d=await r.json();
    if(d.success){
      if(!D[user].includes(friend))D[user].push(friend);
      if(bidi&&!D[friend].includes(user))D[friend].push(user);
      render();toast(d.message,'ok');
    }else toast(d.message,'er');
  }catch(e){toast('Server error','er');}
}

async function removeUser(name){
  try{await fetch(API+'/api/user/remove',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({username:name})});}catch(e){}
  delete D[name];Object.keys(D).forEach(u=>{D[u]=D[u].filter(f=>f!==name);});render();toast('"'+name+'" removed','in');
}

async function removeFriend(user,friend){
  try{await fetch(API+'/api/friend/remove',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({user,friend})});}catch(e){}
  D[user]=D[user].filter(f=>f!==friend);render();viewUser(user);toast('Removed '+friend,'in');
}

async function clearAll(){
  if(!Object.keys(D).length)return;
  try{await fetch(API+'/api/reset',{method:'POST'});}catch(e){}
  D={};render();toast('All data cleared','in');
}

async function runMutual(){
  const u1=document.getElementById('m1').value,u2=document.getElementById('m2').value;
  const box=document.getElementById('mres');
  if(!u1||!u2){box.innerHTML='';return;}
  if(u1===u2){box.innerHTML=emptyHTML('⚠️','Select two different users');return;}
  try{
    const r=await fetch(API+'/api/mutual?user1='+u1+'&user2='+u2);
    const d=await r.json();
    if(!d.success){box.innerHTML=emptyHTML('❌',d.message);return;}
    renderMutual(box,d,u1,u2);
  }catch(e){box.innerHTML=emptyHTML('❌','Server not responding');}
}

async function runSuggest(){
  const u1=document.getElementById('s1').value,u2=document.getElementById('s2').value;
  const box=document.getElementById('sres');
  if(!u1||!u2){box.innerHTML='';return;}
  if(u1===u2){box.innerHTML=emptyHTML('⚠️','Select two different users');return;}
  try{
    const r=await fetch(API+'/api/suggest?user1='+u1+'&user2='+u2);
    const d=await r.json();
    if(!d.success){box.innerHTML=emptyHTML('❌',d.message);return;}
    renderSuggest(box,d,u1,u2);
  }catch(e){box.innerHTML=emptyHTML('❌','Server not responding');}
}

async function loadDemo(){
  const demo={alice:['bob','charlie','diana'],bob:['alice','charlie','eve'],charlie:['alice','bob','frank'],diana:['alice','eve'],eve:['bob','diana','frank'],frank:['charlie','eve']};
  for(const u of Object.keys(demo)){
    try{await fetch(API+'/api/user/add',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({username:u})});}catch(e){}
    D[u]=D[u]||[];
  }
  for(const[u,friends]of Object.entries(demo)){
    for(const f of friends){
      try{await fetch(API+'/api/friend/add',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({user:u,friend:f,bidirectional:'false'})});}catch(e){}
      if(!D[u].includes(f))D[u].push(f);
    }
  }
  render();toast('Demo loaded!','ok');
}

const PAL=['#f0c040','#2dd4bf','#fb7185','#60a5fa','#a78bfa','#fb923c','#34d399','#f472b6'];
const pal=n=>PAL[hash(n)%PAL.length];
function hash(s){let h=0;for(let i=0;i<s.length;i++)h=(h*31+s.charCodeAt(i))&0xffffffff;return Math.abs(h);}

function render(){renderSidebar();syncSelects();runMutual();runSuggest();renderGraph();renderMatrix();updateStats();}

function renderSidebar(){
  const users=Object.keys(D).sort();
  const ul=document.getElementById('ulist');
  if(!users.length){ul.innerHTML='<div class="empty"><div class="ei">👤</div><p>No users yet.<br/>Add one above.</p></div>';return;}
  ul.innerHTML=users.map(u=>{const c=pal(u);return'<div class="uchip"><div class="av" style="background:'+c+'18;color:'+c+';border:1px solid '+c+'30">'+u[0].toUpperCase()+'</div><span class="cn">'+u+'</span><span class="cm">'+(D[u].length)+' friend'+(D[u].length!==1?'s':'')+'</span><div class="cacts"><button class="btn bgh bxs" onclick="viewUser(\''+u+'\')">View</button><button class="btn br bxs" onclick="removeUser(\''+u+'\')">✕</button></div></div>';}).join('');
}

function syncSelects(){
  const users=Object.keys(D).sort();
  ['f1','f2','m1','m2','s1','s2'].forEach(id=>{
    const sel=document.getElementById(id),cur=sel.value,ph=sel.options[0].text;
    sel.innerHTML='<option value="">'+ph+'</option>'+users.map(u=>'<option value="'+u+'"'+(u===cur?' selected':'')+'>'+u+'</option>').join('');
  });
}

function updateStats(){
  const users=Object.keys(D);let c=0;users.forEach(u=>c+=D[u].length);
  const max=users.length*(users.length-1);
  document.getElementById('sU').textContent=users.length;
  document.getElementById('sC').textContent=c;
  document.getElementById('sD').textContent=max?Math.round(c/max*100)+'%':'0%';
}

function renderMutual(box,d,u1,u2){
  const u1f=D[u1]||[],u2f=D[u2]||[];
  let h='<div class="rcard"><div class="rtop"><span class="rtitle">'+u1+' × '+u2+'</span><span class="badge '+(d.count?'b-gold':'b-muted')+'" style="margin-left:auto">'+d.count+' mutual</span></div>';
  if(!d.count)h+='<div style="font-family:var(--mono);font-size:11px;color:var(--muted);text-align:center;padding:14px">No mutual friends found</div>';
  else h+='<div class="tags">'+d.mutualFriends.map((f,i)=>'<div class="tag t-gold" style="animation-delay:'+(i*.04)+'s"><div style="width:16px;height:16px;border-radius:50%;background:'+pal(f)+'22;color:'+pal(f)+';display:flex;align-items:center;justify-content:center;font-size:9px;font-weight:700">'+f[0].toUpperCase()+'</div>'+f+'</div>').join('')+'</div>';
  h+='<hr style="border:none;border-top:1px solid var(--border);margin:14px 0"/><div class="fgrid"><div><div class="ftitle">'+u1+' ('+u1f.length+')</div><div class="mtags">'+(u1f.length?u1f.map(f=>'<span class="mtag" style="background:rgba(240,192,64,.08);color:var(--gold);border:1px solid rgba(240,192,64,.18)">'+f+'</span>').join(''):'<em style="color:var(--muted)">none</em>')+'</div></div><div><div class="ftitle">'+u2+' ('+u2f.length+')</div><div class="mtags">'+(u2f.length?u2f.map(f=>'<span class="mtag" style="background:rgba(45,212,191,.08);color:var(--teal);border:1px solid rgba(45,212,191,.18)">'+f+'</span>').join(''):'<em style="color:var(--muted)">none</em>')+'</div></div></div></div>';
  box.innerHTML=h;
}

function renderSuggest(box,d,u1,u2){
  let h='<div class="rcard"><div class="rtop"><span class="rtitle">Suggestions for '+u1+' & '+u2+'</span><span class="badge '+(d.count?'b-teal':'b-muted')+'" style="margin-left:auto">'+d.count+' suggested</span></div>';
  if(!d.count)h+='<div style="font-family:var(--mono);font-size:11px;color:var(--muted);text-align:center;padding:14px">No suggestions!</div>';
  else h+='<div class="tags">'+d.suggestions.map((f,i)=>'<div class="tag t-teal" style="animation-delay:'+(i*.04)+'s"><div style="width:16px;height:16px;border-radius:50%;background:'+pal(f)+'22;color:'+pal(f)+';display:flex;align-items:center;justify-content:center;font-size:9px;font-weight:700">'+f[0].toUpperCase()+'</div>'+f+'</div>').join('')+'</div>';
  h+='</div>';box.innerHTML=h;
}

function emptyHTML(icon,msg){return'<div class="empty"><div class="ei">'+icon+'</div><p>'+msg+'</p></div>';}

function viewUser(name){
  document.getElementById('mt').textContent=name.toUpperCase();
  document.getElementById('ms').textContent=(D[name].length)+' friend(s) in HashSet';
  const friends=D[name]||[];
  document.getElementById('mb').innerHTML=!friends.length
    ?'<p style="font-family:var(--mono);font-size:11px;color:var(--muted)">No friends yet.</p>'
    :friends.sort().map(f=>'<div class="mfrow"><div style="display:flex;align-items:center;gap:7px"><div style="width:22px;height:22px;border-radius:50%;background:'+pal(f)+'20;color:'+pal(f)+';display:flex;align-items:center;justify-content:center;font-size:10px;font-weight:700">'+f[0].toUpperCase()+'</div>'+f+'</div><button class="btn br bxs" onclick="removeFriend(\''+name+'\',\''+f+'\')">Remove</button></div>').join('');
  document.getElementById('ov').classList.add('open');
}
function closeModal(){document.getElementById('ov').classList.remove('open');}

let GN=[],GE=[],GD=null,GOX,GOY,GF;
function renderGraph(){
  const cv=document.getElementById('gc');const users=Object.keys(D);
  const prev={};GN.forEach(n=>prev[n.id]={x:n.x,y:n.y});
  GN=users.map((u,i)=>({id:u,color:pal(u),vx:0,vy:0,x:prev[u]?.x??cv.clientWidth/2+Math.cos(2*Math.PI*i/users.length)*155,y:prev[u]?.y??cv.clientHeight/2+Math.sin(2*Math.PI*i/users.length)*155}));
  const seen=new Set();GE=[];
  users.forEach(u=>(D[u]||[]).forEach(f=>{if(!users.includes(f))return;const k=[u,f].sort().join('|');if(!seen.has(k)){seen.add(k);GE.push({a:u,b:f});}}));
  if(GF)cancelAnimationFrame(GF);gLoop();
}
function gLoop(){
  const cv=document.getElementById('gc');if(!cv)return;
  const rect=cv.parentElement.getBoundingClientRect();cv.width=rect.width;cv.height=rect.height;
  const ctx=cv.getContext('2d');
  if(GN.length>1){
    const K=125;GN.forEach(n=>{n.vx=0;n.vy=0;});
    for(let i=0;i<GN.length;i++)for(let j=i+1;j<GN.length;j++){const dx=GN[j].x-GN[i].x,dy=GN[j].y-GN[i].y,d=Math.max(Math.hypot(dx,dy),1),f=K*K/d;GN[i].vx-=f*dx/d;GN[i].vy-=f*dy/d;GN[j].vx+=f*dx/d;GN[j].vy+=f*dy/d;}
    GE.forEach(e=>{const a=GN.find(n=>n.id===e.a),b=GN.find(n=>n.id===e.b);if(!a||!b)return;const dx=b.x-a.x,dy=b.y-a.y,d=Math.max(Math.hypot(dx,dy),1),f=d*d/(K*2.5);a.vx+=f*dx/d;a.vy+=f*dy/d;b.vx-=f*dx/d;b.vy-=f*dy/d;});
    GN.forEach(n=>{n.vx+=(cv.width/2-n.x)*.011;n.vy+=(cv.height/2-n.y)*.011;});
    GN.forEach(n=>{if(n!==GD){n.x+=n.vx*.27;n.y+=n.vy*.27;n.x=Math.max(44,Math.min(cv.width-44,n.x));n.y=Math.max(44,Math.min(cv.height-44,n.y));}});
  }
  ctx.clearRect(0,0,cv.width,cv.height);
  GE.forEach(e=>{const a=GN.find(n=>n.id===e.a),b=GN.find(n=>n.id===e.b);if(!a||!b)return;ctx.beginPath();ctx.moveTo(a.x,a.y);ctx.lineTo(b.x,b.y);ctx.strokeStyle='rgba(240,192,64,.18)';ctx.lineWidth=1.5;ctx.stroke();});
  GN.forEach(n=>{const R=21;const g=ctx.createRadialGradient(n.x,n.y,R*.4,n.x,n.y,R*2);g.addColorStop(0,n.color+'28');g.addColorStop(1,'transparent');ctx.beginPath();ctx.arc(n.x,n.y,R*2,0,Math.PI*2);ctx.fillStyle=g;ctx.fill();ctx.beginPath();ctx.arc(n.x,n.y,R,0,Math.PI*2);ctx.fillStyle=n.color+'18';ctx.fill();ctx.strokeStyle=n.color;ctx.lineWidth=1.8;ctx.stroke();ctx.fillStyle=n.color;ctx.font='bold 12px Space Grotesk,sans-serif';ctx.textAlign='center';ctx.textBaseline='middle';ctx.fillText(n.id[0].toUpperCase(),n.x,n.y);ctx.fillStyle='#e2e4ef';ctx.font='10.5px Fira Code,monospace';ctx.fillText(n.id,n.x,n.y+R+11);});
  GF=requestAnimationFrame(gLoop);
}
const gc=document.getElementById('gc');
gc.addEventListener('mousedown',e=>{const r=gc.getBoundingClientRect(),mx=e.clientX-r.left,my=e.clientY-r.top;GN.forEach(n=>{if(Math.hypot(mx-n.x,my-n.y)<25){GD=n;GOX=n.x-mx;GOY=n.y-my;}});});
document.addEventListener('mousemove',e=>{if(!GD)return;const r=gc.getBoundingClientRect();GD.x=e.clientX-r.left+GOX;GD.y=e.clientY-r.top+GOY;});
document.addEventListener('mouseup',()=>GD=null);

function renderMatrix(){
  const users=Object.keys(D).sort();const w=document.getElementById('matrix');
  if(!users.length){w.innerHTML=emptyHTML('📊','Add users to see the matrix');return;}
  const cs=Math.min(52,Math.floor(540/(users.length+1)));
  let h='<div style="font-family:var(--mono);font-size:9px;color:var(--muted);letter-spacing:2px;text-transform:uppercase;margin-bottom:14px">Adjacency Matrix</div><table><tr><th style="width:'+cs+'px;height:'+cs+'px"></th>'+users.map(u=>'<th style="width:'+cs+'px;height:'+cs+'px;color:var(--muted);font-family:var(--mono);font-size:'+Math.max(8,cs/5.5)+'px;writing-mode:vertical-rl;transform:rotate(180deg);padding:4px;font-weight:500">'+u+'</th>').join('')+'</tr>'+users.map(u1=>'<tr><th style="text-align:right;padding-right:9px;color:var(--muted);font-family:var(--mono);font-size:'+Math.max(8,cs/5.5)+'px;font-weight:500;white-space:nowrap">'+u1+'</th>'+users.map(u2=>{if(u1===u2)return'<td style="background:var(--faint);border:1px solid var(--border);text-align:center;color:var(--muted);font-size:13px">—</td>';const s1=new Set(D[u1]||[]),s2=new Set(D[u2]||[]);const m=[...s1].filter(f=>s2.has(f));const fr=(D[u1]||[]).includes(u2);const sym=fr?'✓':m.length?m.length:'·';const bg=fr?'rgba(240,192,64,.14)':m.length?'rgba(45,212,191,'+Math.min(.13,m.length*.04+.03)+')':'transparent';const col=fr?'var(--gold)':m.length?'var(--teal)':'var(--faint)';return'<td style="background:'+bg+';border:1px solid var(--border);text-align:center;color:'+col+';font-weight:700;width:'+cs+'px;height:'+cs+'px;font-family:var(--mono);font-size:'+Math.max(11,cs/4)+'px;cursor:default">'+sym+'</td>';}).join('')+'</tr>').join('')+'</table><div style="display:flex;gap:18px;margin-top:12px;font-family:var(--mono);font-size:10.5px;color:var(--muted)"><span><span style="color:var(--gold);font-weight:700">✓</span> Friends</span><span><span style="color:var(--teal);font-weight:700">N</span> Mutual</span><span><span style="color:var(--faint)">·</span> No link</span></div>';
  w.innerHTML=h;
}

function switchTab(n){
  const ns=['mutual','suggest','graph','matrix'];
  document.querySelectorAll('.tab').forEach((t,i)=>t.classList.toggle('active',ns[i]===n));
  document.querySelectorAll('.pane').forEach(p=>p.classList.remove('active'));
  document.getElementById('p-'+n).classList.add('active');
  if(n==='graph')renderGraph();if(n==='matrix')renderMatrix();
}
function toast(msg,type='in'){
  const el=document.getElementById('toast');el.textContent=msg;el.className='show '+type;
  clearTimeout(el._t);el._t=setTimeout(()=>el.classList.remove('show'),2700);
}

load();
</script>
</body>
</html>
""";
    }
}
