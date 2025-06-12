(function() {
  const menuEl = document.querySelector('.menu');
  const cartItemsEl = document.querySelector('.cart-items');
  const totalPriceEl = document.getElementById('total-price');
  const etaEl = document.getElementById('eta');
  const placeOrderBtn = document.getElementById('place-order');
  let coffeesData = [];
  const cart = {};

  async function initMenu() {
    try {
      const res = await fetch('/api/coffees');
      if (!res.ok) throw new Error('Failed to load coffees');
      coffeesData = await res.json();
      menuEl.innerHTML = '';
      coffeesData.forEach(coffee => {
        const card = document.createElement('div');
        card.className = 'coffee-card';
        card.innerHTML = `
          <img src="${coffee.imagePath}" alt="${coffee.name}">
          <h2>${coffee.name}</h2>
          <p class="price">$${coffee.price.toFixed(2)}</p>
          <p class="desc">${coffee.description}</p>
          <button class="btn primary add-btn" data-id="${coffee.id}">Add</button>
        `;
        menuEl.appendChild(card);
      });
    } catch (e) {
      console.error(e);
      menuEl.innerHTML = '<p>Error loading coffees.</p>';
    }
  }

  function renderCart() {
    cartItemsEl.innerHTML = '';
    let total = 0, totalEta = 0;
    Object.values(cart).forEach(({coffee, qty}) => {
      total += coffee.price * qty;
      totalEta += coffee.estimatedTime * qty;
      const li = document.createElement('li');
      li.innerHTML = `
        <span>${coffee.name} × ${qty} - $${(coffee.price * qty).toFixed(2)}</span>
        <button class="remove-btn" data-id="${coffee.id}">×</button>
      `;
      cartItemsEl.appendChild(li);
    });
    totalPriceEl.textContent = `$${total.toFixed(2)}`;
    etaEl.textContent = `${totalEta.toFixed(1)} mins`;
    placeOrderBtn.disabled = Object.keys(cart).length === 0;
  }

  function setupEvents() {
    menuEl && menuEl.addEventListener('click', e => {
      if (e.target.matches('.add-btn')) {
        const id = e.target.dataset.id;
        const coffee = coffeesData.find(c => c.id == id);
        if (!coffee) return;
        cart[id] = cart[id] ? {coffee, qty: cart[id].qty + 1} : {coffee, qty: 1};
        renderCart();
      }
    });

    cartItemsEl && cartItemsEl.addEventListener('click', e => {
      if (e.target.matches('.remove-btn')) {
        delete cart[e.target.dataset.id];
        renderCart();
      }
    });

    placeOrderBtn && placeOrderBtn.addEventListener('click', async () => {
      const ids = [];
      Object.values(cart).forEach(({coffee, qty}) => {
        for (let i = 0; i < qty; i++) ids.push(coffee.id);
      });
      try {
        const resp = await fetch('/api/orders', {
          method: 'POST',
          headers: {'Content-Type': 'application/json'},
          body: JSON.stringify(ids)
        });
        if (!resp.ok) throw new Error();
        Object.keys(cart).forEach(k => delete cart[k]);
        renderCart();
        alert('Order placed successfully!');
      } catch {
        alert('Failed to place order.');
      }
    });
  }

  async function initOrders() {
    const ordersList = document.querySelector('.orders-list');
    if (!ordersList) return;
    try {
      const res = await fetch('/api/orders');
      if (!res.ok) throw new Error();
      const orders = await res.json();
      ordersList.innerHTML = '';
      orders.forEach(order => {
        const card = document.createElement('div'); card.className = 'order-card';
        const header = document.createElement('div'); header.className = 'order-header';
        header.innerHTML = `
          <h3>Order #${String(order.id).padStart(5,'0')}</h3>
          <time datetime="${order.dateTime}">${order.dateTime.replace('T',' ')}</time>
        `;
        card.appendChild(header);
        const counts = {};
        order.coffees.forEach(c => {
          counts[c.id] = counts[c.id] || {c, qty:0}; counts[c.id].qty++;
        });
        const itemsUl = document.createElement('ul'); itemsUl.className='order-items';
        Object.values(counts).forEach(({c,qty}) => {
          const li=document.createElement('li');
          li.innerHTML = `<span>${c.name} × ${qty}</span><span>$${(c.price*qty).toFixed(2)}</span>`;
          itemsUl.appendChild(li);
        });
        card.appendChild(itemsUl);
        const summary = document.createElement('div'); summary.className='order-summary';
        summary.innerHTML = `<p>Total: <strong>$${order.totalPrice.toFixed(2)}</strong></p><p>ETA: <span>${order.estimatedTime} mins</span></p>`;
        card.appendChild(summary);
        const btn = document.createElement('button'); btn.className='btn delete-btn'; btn.textContent='×';
        btn.addEventListener('click', async () => {
          if (!confirm('Delete this order?')) return;
          const r = await fetch(`/api/orders/${order.id}`, {method:'DELETE'});
          if (r.status===204) card.remove(); else alert('Delete failed');
        });
        card.appendChild(btn);
        ordersList.appendChild(card);
      });
    } catch {
      ordersList.innerHTML = '<p>Error loading orders.</p>';
    }
  }

  document.addEventListener('DOMContentLoaded', () => {
    if (menuEl) { initMenu().then(renderCart).then(setupEvents); }
    initOrders();
  });
})();
