const Search = ((element) => {
  const removeSearchBtn = $('#search-remove-search');
  const searchInput = $('#search-input');

  function init() {
    removeSearchBtn.click(onRemoveSearchClick);
    searchInput.keyup(onSearchInputKeyup);
  }

  function onRemoveSearchClick() {
    searchInput.focus().val(null);
    removeSearchBtn.hide();
  }

  function onSearchInputKeyup() {
    if (searchInput.val().length > 0) {
      removeSearchBtn.show();
    } else {
      removeSearchBtn.hide();
    }
  }

  init();

  return element;
})({});


