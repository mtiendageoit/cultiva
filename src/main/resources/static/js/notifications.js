const Notifications = ((element) => {
  const loading = $('#loadingContainer');
  const messageContainer = $('#notMessage');

  element.loading = (show) => {
    show ? loading.slideDown('slow') : loading.slideUp('slow', () => messageContainer.text('Procesando'));
  };

  element.showNotification = (message) => {
    messageContainer.text(message);
    element.loading(true);
  };

  return element;
})({});