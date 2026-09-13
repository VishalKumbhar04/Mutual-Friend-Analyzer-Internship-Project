/**
 * NetGraph — Social Network Analyzer
 * script.js — Frontend Logic
 *
 * Communicates with Spring Boot backend via Fetch API.
 * Base URL: http://localhost:8080
 */

const API = 'http://localhost:8080';

// ═══════════════════════════════════════════════════════════════════
// NAVIGATION
// ═══════════════════════════════════════════════════════════════════

const pageMeta = {
  'dashboard':   { title: 'Dashboard',            subtitle: 'Your network at a glance' },
  'add-user':    { title: 'Add User',              subtitle: 'Register a new node in the graph' },
  'add-friend':  { title: 'Add Friend',            subtitle: 'Create a bidirectional edge between two users' },
  'mutual':      { title: 'Mutual Friends',        subtitle: 'Set Intersection using retainAll() + TreeSet' },
  'recommend':   { title: 'Recommendations',       subtitle: 'Friends-of-friends ranked by mutual connections' },
  'explore':     { title: 'Explore Network',       subtitle: 'Browse a user\'s full friend list' },
};

document.querySelectorAll('.nav-item').forEach(btn => {
  btn.addEventListener('click', () => {
    const page = btn.dataset.page;
    navigateTo(page);
  });
});

function navigateTo(page) {
  // Update nav
  document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('active'));
  document.querySelector(`.nav-item[data-page="${page}"]`)?.classList.add('active');

  // Update content
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.getElementById(`page-${page}`)?.classList.add('active');

  // Update topbar
  const meta = pageMeta[page] || {};
  document.getElementById('pageTitle').textContent = meta.title || page;
  document.getElementById('pageSubtitle').textContent = meta.subtitle || '';

  // Refresh page-specific data
  if (page === 'dashboard') loadDashboard();
  if (page === 'add-user')  loadAllUsersPage();
  if (page === 'add-friend') populateAllDropdowns();
  if (page === 'mutual')    populateDropdowns(['mutualUser1','mutualUser2']);
  if (page === 'recommend') populateDropdowns(['recommendUser']);
  if (page === 'explore')   populateDropdowns(['exploreUser']);
}

// ═══════════════════════════════════════════════════════════════════
// UTILITIES
// ═══════════════════════════════════════════════════════════════════

function avatar(name) {
  return name ? name.charAt(0).toUpperCase() : '?';
}

function showToast(elId, msg, type = 'success') {
  const el = document.getElementById(elId);
  el.textContent = msg;
  el.className = `toast show ${type}`;
  setTimeout(() => { el.className = 'toast'; }, 4000);
}

function emptyState(msg = 'No results found') {
  return `<div class="empty-state">
    <div class="empty-icon">◎</div>
    <div class="empty-text">${msg}</div>
  </div>`;
}

async function apiFetch(path, options = {}) {
  const res = await fetch(`${API}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  });
  return res.json();
}

// ═══════════════════════════════════════════════════════════════════
// DROPDOWNS — Populate all <select> elements with current users
// ═══════════════════════════════════════════════════════════════════

async function populateDropdowns(ids) {
  try {
    const data = await apiFetch('/users');
    const users = data.users || [];
    ids.forEach(id => {
      const sel = document.getElementById(id);
      if (!sel) return;
      const current = sel.value;
      sel.innerHTML = '<option value="">— Select User —</option>';
      users.forEach(u => {
        const opt = document.createElement('option');
        opt.value = u;
        opt.textContent = u;
        if (u === current) opt.selected = true;
        sel.appendChild(opt);
      });
    });
  } catch (e) {
    console.error('Failed to populate dropdowns:', e);
  }
}

async function populateAllDropdowns() {
  await populateDropdowns(['friendUserA','friendUserB','mutualUser1','mutualUser2','recommendUser','exploreUser']);
}

// ═══════════════════════════════════════════════════════════════════
// DASHBOARD
// ═══════════════════════════════════════════════════════════════════

async function loadDashboard() {
  try {
    const stats = await apiFetch('/stats');
    document.getElementById('statUsers').textContent = stats.totalUsers ?? '—';
    document.getElementById('statConnections').textContent = stats.totalConnections ?? '—';

    // Top users
    const topEl = document.getElementById('topUsers');
    const topUsers = stats.topUsers || [];
    if (topUsers.length === 0) {
      topEl.innerHTML = emptyState('No users yet');
    } else {
      topEl.innerHTML = topUsers.map((u, i) => `
        <div class="top-user-row">
          <span class="top-user-rank">#${i+1}</span>
          <div class="top-user-avatar">${avatar(u.username)}</div>
          <span class="top-user-name">${u.username}</span>
          <span class="top-user-count">${u.friendCount} friends</span>
        </div>
      `).join('');
    }

    // All users chips
    const usersData = await apiFetch('/users');
    const allUsers = usersData.users || [];
    document.getElementById('allUsersBadge').textContent = `${allUsers.length} members`;
    document.getElementById('allUsersList').innerHTML = allUsers.length
      ? allUsers.map(u => userChip(u)).join('')
      : emptyState('No users registered');

  } catch (e) {
    console.error('Dashboard load failed:', e);
  }
}

function userChip(name) {
  return `<div class="user-chip">
    <div class="chip-avatar">${avatar(name)}</div>
    <span>${name}</span>
  </div>`;
}

// ═══════════════════════════════════════════════════════════════════
// ADD USER
// ═══════════════════════════════════════════════════════════════════

document.getElementById('addUserBtn').addEventListener('click', async () => {
  const input = document.getElementById('newUsername');
  const username = input.value.trim();
  if (!username) {
    showToast('addUserToast', 'Please enter a username.', 'error');
    return;
  }
  try {
    const data = await apiFetch('/users', {
      method: 'POST',
      body: JSON.stringify({ username })
    });
    showToast('addUserToast', data.message, data.success ? 'success' : 'error');
    if (data.success) {
      input.value = '';
      loadAllUsersPage();
      loadDashboard();
    }
  } catch (e) {
    showToast('addUserToast', 'Failed to connect to server. Is Spring Boot running?', 'error');
  }
});

document.getElementById('newUsername').addEventListener('keydown', e => {
  if (e.key === 'Enter') document.getElementById('addUserBtn').click();
});

async function loadAllUsersPage() {
  try {
    const data = await apiFetch('/users');
    const users = data.users || [];
    document.getElementById('addPageUserList').innerHTML = users.length
      ? users.map(u => userChip(u)).join('')
      : emptyState('No users yet. Add your first one!');
  } catch (e) {}
}

// ═══════════════════════════════════════════════════════════════════
// ADD FRIEND
// ═══════════════════════════════════════════════════════════════════

document.getElementById('addFriendBtn').addEventListener('click', async () => {
  const userA = document.getElementById('friendUserA').value;
  const userB = document.getElementById('friendUserB').value;
  if (!userA || !userB) {
    showToast('addFriendToast', 'Please select both users.', 'error');
    return;
  }
  if (userA === userB) {
    showToast('addFriendToast', 'A user cannot be friends with themselves.', 'error');
    return;
  }
  try {
    const data = await apiFetch('/friends', {
      method: 'POST',
      body: JSON.stringify({ userA, userB })
    });
    showToast('addFriendToast', data.message, data.success ? 'success' : 'error');
  } catch (e) {
    showToast('addFriendToast', 'Failed to connect to server.', 'error');
  }
});

// ═══════════════════════════════════════════════════════════════════
// MUTUAL FRIENDS
// Set Intersection: friends1.retainAll(friends2) → TreeSet
// ═══════════════════════════════════════════════════════════════════

document.getElementById('findMutualBtn').addEventListener('click', async () => {
  const user1 = document.getElementById('mutualUser1').value;
  const user2 = document.getElementById('mutualUser2').value;
  if (!user1 || !user2) {
    alert('Please select both users.');
    return;
  }
  if (user1 === user2) {
    alert('Please select two different users.');
    return;
  }
  try {
    const data = await apiFetch(`/mutual?user1=${encodeURIComponent(user1)}&user2=${encodeURIComponent(user2)}`);
    const panel = document.getElementById('mutualResult');
    const list  = document.getElementById('mutualList');
    const count = document.getElementById('mutualCount');

    document.getElementById('mutualTitle').textContent = `${user1}  ∩  ${user2}`;
    count.textContent = data.count ?? 0;
    panel.style.display = 'block';

    const mutual = data.mutualFriends || [];
    if (mutual.length === 0) {
      list.innerHTML = emptyState(`No mutual friends between ${user1} and ${user2}`);
    } else {
      list.innerHTML = mutual.map(u => `
        <div class="mutual-item">
          <div class="mutual-avatar">${avatar(u)}</div>
          <span class="mutual-name">${u}</span>
          <span class="mutual-badge">Mutual ✓</span>
        </div>
      `).join('');
    }
  } catch (e) {
    alert('Failed to connect to server.');
  }
});

// ═══════════════════════════════════════════════════════════════════
// RECOMMENDATIONS
// Friends-of-friends scored by mutual connection count
// ═══════════════════════════════════════════════════════════════════

document.getElementById('getRecommendBtn').addEventListener('click', async () => {
  const user = document.getElementById('recommendUser').value;
  if (!user) { alert('Please select a user.'); return; }

  try {
    const data = await apiFetch(`/recommend/${encodeURIComponent(user)}`);
    const panel = document.getElementById('recommendResult');
    const list  = document.getElementById('recommendList');
    const count = document.getElementById('recommendCount');

    document.getElementById('recommendTitle').textContent = `Suggestions for ${user}`;
    count.textContent = data.count ?? 0;
    panel.style.display = 'block';

    const recs = data.recommendations || [];
    if (recs.length === 0) {
      list.innerHTML = emptyState(`No recommendations for ${user} — they may already know everyone!`);
    } else {
      // Find max score for bar scaling
      const maxScore = Math.max(...recs.map(r => r.mutualCount));
      list.innerHTML = recs.map(r => {
        const pct = maxScore > 0 ? Math.round((r.mutualCount / maxScore) * 100) : 0;
        return `
          <div class="rec-item">
            <div class="rec-avatar">${avatar(r.username)}</div>
            <div class="rec-info">
              <div class="rec-name">${r.username}</div>
              <div class="rec-sub">${r.mutualCount} mutual friend${r.mutualCount !== 1 ? 's' : ''}</div>
            </div>
            <div class="rec-score">
              <div class="score-bar-container">
                <div class="score-bar" style="width: ${pct}%"></div>
              </div>
              <div class="score-label">Score: ${r.mutualCount}</div>
            </div>
          </div>
        `;
      }).join('');
    }
  } catch (e) {
    alert('Failed to connect to server.');
  }
});

// ═══════════════════════════════════════════════════════════════════
// EXPLORE NETWORK
// ═══════════════════════════════════════════════════════════════════

document.getElementById('exploreBtn').addEventListener('click', async () => {
  const user = document.getElementById('exploreUser').value;
  if (!user) { alert('Please select a user.'); return; }

  try {
    const data = await apiFetch(`/friends/${encodeURIComponent(user)}`);
    const panel = document.getElementById('exploreResult');
    const list  = document.getElementById('exploreList');

    document.getElementById('exploreTitle').textContent = `${user}'s Friends`;
    document.getElementById('exploreCount').textContent = data.count ?? 0;
    panel.style.display = 'block';

    const friends = data.friends || [];
    if (friends.length === 0) {
      list.innerHTML = emptyState(`${user} has no friends yet. Use "Add Friend" to connect them!`);
    } else {
      list.innerHTML = friends.map(f => `
        <div class="mutual-item">
          <div class="mutual-avatar">${avatar(f)}</div>
          <span class="mutual-name">${f}</span>
          <span class="mutual-badge">Friend ✓</span>
        </div>
      `).join('');
    }
  } catch (e) {
    alert('Failed to connect to server.');
  }
});

// ═══════════════════════════════════════════════════════════════════
// GLOBAL SEARCH
// ═══════════════════════════════════════════════════════════════════

const searchInput  = document.getElementById('globalSearch');
const searchResults = document.getElementById('searchResults');
let searchTimer;

searchInput.addEventListener('input', () => {
  clearTimeout(searchTimer);
  const q = searchInput.value.trim();
  if (q.length < 1) {
    searchResults.classList.remove('show');
    return;
  }
  searchTimer = setTimeout(async () => {
    try {
      const data = await apiFetch(`/users/search?query=${encodeURIComponent(q)}`);
      const results = data.results || [];
      if (results.length === 0) {
        searchResults.innerHTML = '<div class="search-result-item" style="color:#888">No users found</div>';
      } else {
        searchResults.innerHTML = results.map(u => `
          <div class="search-result-item" onclick="onSearchSelect('${u}')">
            ${avatar(u)} ${u}
          </div>
        `).join('');
      }
      searchResults.classList.add('show');
    } catch (e) {}
  }, 250);
});

document.addEventListener('click', e => {
  if (!e.target.closest('.search-bar')) {
    searchResults.classList.remove('show');
  }
});

function onSearchSelect(username) {
  searchResults.classList.remove('show');
  searchInput.value = '';
  // Navigate to explore and pre-select user
  navigateTo('explore');
  setTimeout(() => {
    const sel = document.getElementById('exploreUser');
    sel.value = username;
    document.getElementById('exploreBtn').click();
  }, 200);
}

// ═══════════════════════════════════════════════════════════════════
// INIT — Load dashboard on startup
// ═══════════════════════════════════════════════════════════════════

document.addEventListener('DOMContentLoaded', () => {
  loadDashboard();
});
