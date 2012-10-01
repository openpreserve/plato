function updateBalancedNodeData(currentNode, data) {
	var parent = currentNode.getParent();
	if (!parent) {
		return

	}

	for ( var i = 0; i < data.length; i++) {
		var child = RichFaces.$(currentNode.getId().replace(
				/\.[0-9]+:treeNode/, '.' + i + ':treeNode'));
		if (child && child != currentNode) {
			var slider = RichFaces.$(child.getId().replace('treeNode',
					'weightSlider'));
			if (slider) {
				// slider.__setValue(data[i].weight,
				// null, true);
				var tmpOnChangeHandler = slider.onchange;
				slider.onchange = null;
				slider.setValue(data[i].weight);
				slider.onchange = tmpOnChangeHandler;
			}
		}
	}
}