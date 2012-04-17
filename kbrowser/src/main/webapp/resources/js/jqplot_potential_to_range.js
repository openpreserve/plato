/**
 * 
 * 
 * @param targetId
 * @param data
 */
function plotPotentialToRangeMax(targetId, data) {
	var values = new Array();
	var seriesOptions = new Array();

	// Add series
	for ( var i in data.seriesData) {
		values.push([ data.seriesData[i] ]);
	}

	// Add series labels
	for ( var i in data.seriesLabels) {
		seriesOptions.push({
			label : data.seriesLabels[i]
		});
	}

	// Set empty label if none specified
	if (data.sourceLabels.length == 0) {
		data.sourceLabels.push(' ');
	}

	// Plot diagram
	var plot = $.jqplot(targetId, values, {
		title : data.title,
		seriesColors : [ "#998b63", "#c5b47f", "#3A8B99", "#4bb2c5" ],
		seriesDefaults : {
			renderer : $.jqplot.BarRenderer,
			rendererOptions : {
				highlightMouseOver : false
			},
			pointLabels : {
				show : true,
				formatString : data.formatString
			},
			shadow : false
		},
		series : seriesOptions,
		legend : {
			show : true,
			placement : "inside"
		},
		axes : {
			xaxis : {
				renderer : $.jqplot.CategoryAxisRenderer,
				label : data.sourcesTitle,
				ticks : data.sourceLabels,
			},
			yaxis : {
				min : 0,
				max : data.maxValue,
				tickOptions : {
					formatString : data.formatString,
				}
			}
		}
	}).replot();;
}

/**
 * Plots the potential to range data onto the canvas identified by targetId.
 * 
 * @param targetId The target canvas
 * @param data The data
 * @param autosize Whether the size of the target canvas should be automatically computed
 * @param 
 */
function plotPotentialToRangeHorizontal(targetId, data, autosize, showCriteriaSetCaptions) {
	var values = new Array();
	var seriesOptions = new Array();

	// Flip values for horizontal display
	convertPotentialToRangeHorizontal(data);

	var targetDiv = document.getElementById(targetId);

	// Calculate height depending on number of entries
	if (autosize) {
		targetDiv.style.height = 100 + data.potentialSeries.length * 36 + "px";
	}

	// Add data
	values.push(data.rangeSeries);
	values.push(data.potentialSeries);

	// Add series labels
	for ( var i in data.seriesLabels) {
		seriesOptions.push({
			label : data.seriesLabels[i]
		});
	}
	
	// Plot diagram
	var plot = $.jqplot(targetId, values, {
		title : data.title,
		seriesColors : [ "#4bb2c5", "#c5b47f" ],
		seriesDefaults : {
			renderer : $.jqplot.BarRenderer,
			rendererOptions : {
				highlightMouseOver : false,
				barDirection : 'horizontal'
			},
			pointLabels : {
				show : true,
				formatString : data.formatString
			},
			shadow : false,
		},
		series : seriesOptions,
		legend : {
			show : true,
			placement : "inside",
			location : "se"
		},
		axes : {
			yaxis : {
				renderer : $.jqplot.CategoryAxisRenderer,
				label : data.sourcesTitle,
				ticks : data.sourceLabels,
				showLabel:  false,
				showTicks: showCriteriaSetCaptions,
				labelRenderer : $.jqplot.CanvasAxisLabelRenderer,
				labelOptions : {
					fontSize : '1em',
					textColor : 'black'
				}
			},
			xaxis : {
				min : 0,
				max : data.maxValue,
				tickOptions : {
					formatString : data.formatString
				}
			}
		}
	}).replot();
}

function convertPotentialToRangeHorizontal(data) {

	// Flip potential series
	var potentialSeries = new Array();
	for ( var i = 0; i < data.potentialSeries.length; i++) {
		potentialSeries.push([ data.potentialSeries[i],
				data.potentialSeries.length - i ]);
	}
	data.potentialSeries = potentialSeries;

	// Flip range series
	var rangeSeries = new Array();
	for ( var i = 0; i < data.rangeSeries.length; i++) {
		rangeSeries.push([ data.rangeSeries[i], data.rangeSeries.length - i ]);
	}
	data.rangeSeries = rangeSeries;

	// Flip sourceLabels
	data.sourceLabels.reverse();

	// Flip series labels
	data.seriesLabels.reverse();
}