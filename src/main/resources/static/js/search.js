const Search = ((element) => {
  const removeSearchBtn = $('#search-remove-search');
  const searchInput = $('#search-input');
  const gAutocompleteOptions = {
    componentRestrictions: { country: "mx" },
    fields: ["geometry", "name"],
    strictBounds: false,
  };
  const googleAutocomplete = new google.maps.places.Autocomplete(searchInput[0], gAutocompleteOptions);

  function init() {
    removeSearchBtn.click(onRemoveSearchClick);
    searchInput.keyup(onSearchInputKeyup);
    googleAutocomplete.addListener("place_changed", onAutocompleteChange);
  }

  function onAutocompleteChange() {
    const place = googleAutocomplete.getPlace();
    if (place.geometry && place.geometry.viewport) {
      const viewport = place.geometry.viewport;

      const extent = ol.extent.boundingExtent([
        ol.proj.fromLonLat([viewport.getSouthWest().lng(), viewport.getSouthWest().lat()]),
        ol.proj.fromLonLat([viewport.getNorthEast().lng(), viewport.getNorthEast().lat()])
      ]);
      olMap.getView().fit(extent);
    }
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


