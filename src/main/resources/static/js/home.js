const SearchBar = ((element) => {
  const searchControl = $('#searchControl');
  const cleanSearchBtn = $('#cleanSearchBtn');

  function init() {
    searchControl.keyup(onSearchControlKeyup);
    cleanSearchBtn.click(onCleanSearhBtnClick);
  }

  function onSearchControlKeyup() {
    const filter = searchControl.val();
    Fields.filter(filter);
  }

  function onCleanSearhBtnClick() {
    searchControl.focus().val(null);
    onSearchControlKeyup();
  }

  init();

  return element;
})({});