var ie = /* @cc_on!@ */false;

if (typeof jsf !== 'undefined') {
	jsf.ajax
			.addOnEvent(function(data) {
				if (data.status == "success") {
					var viewState = data.responseXML
							.getElementById("javax.faces.ViewState").firstChild.nodeValue;

					for ( var i = 0; i < document.forms.length; i++) {
						var form = document.forms[i];

						if (!hasViewState(form)) {
							var hidden = document
									.createElement(ie ? "<input name='javax.faces.ViewState'>"
											: "input");
							hidden.setAttribute("type", "hidden");
							hidden
									.setAttribute("name",
											"javax.faces.ViewState");
							hidden.setAttribute("value", viewState);
							hidden.setAttribute("autocomplete", "off");
							form.appendChild(hidden);
						}
					}
				}
			});
}

function hasViewState(form) {
	for ( var i = 0; i < form.elements.length; i++) {
		if (form.elements[i].name == "javax.faces.ViewState") {
			return true;
		}
	}

	return false;
}