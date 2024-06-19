const Notifications = ((element) => {
  const loading = $('#loadingContainer');

  element.loading = (show) => {
    show ? loading.slideDown('slow') : loading.slideUp('slow');
  };

  return element;
})({});