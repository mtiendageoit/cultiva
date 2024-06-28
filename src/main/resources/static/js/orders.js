const Orders = ((element) => {
  let timer;
  const refreshMillis = 20_000; //20 seconds

  element.getOrdersAndCount = (field, callback) => {
    if (timer) {
      clearInterval(timer);
    }

    getOrdersAndCount(field?.uuid, (response) => {
      if (callback) { callback(response); }
      enableAutoRefresh();
    });
  };

  function enableAutoRefresh() {
    timer = setInterval(() => {
      const field = OlMapField.getSelectedField();
      getOrdersAndCount(field?.uuid, (_) => { })
    }, refreshMillis);
  }

  function getOrdersAndCount(fieldUuid, callback) {
    let url = 'api/orders/count-field';
    if (fieldUuid) {
      url += `?fieldUuid=${fieldUuid}`;
    }
    $.get(url, (response) => {
      if (response.fieldOrders) {
        response.fieldOrders.forEach(order => (order.fieldUuid = fieldUuid));
      }
      if (callback) { callback(response); }
      Calendar.showOrders(response.fieldOrders);
      showUIFieldOrderProcessing(response.ordersCount);
    });
  }

  function showUIFieldOrderProcessing(orders) {
    $('#userFieldsList .field-list-item').each((_, item) => {
      const uuid = $(item).attr('uuid');
      const order = orders.find(item => item.fieldUuid === uuid);
      const processingLink = $(`#field-processing-${uuid}`);
      const processingProgess = $(`#field-progress-${uuid}`);
      if (order) {
        processingLink.text(`Procesando (${order.count})`).show();
        processingProgess.show();
      } else {
        processingLink.text('').hide();
        processingProgess.hide();
      }
    });
  }

  return element;
})({});