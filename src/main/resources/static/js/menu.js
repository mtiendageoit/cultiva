const Menu = ((element) => {
  const menuShowOrders = $('#menuShowOrders');

  function init() {
    menuShowOrders.click(onMenuShowOrdersClick)
  }

  function onMenuShowOrdersClick() {
    Orders.showOrdersField(-1, -1);

  }

  init();

  return element;
})({});