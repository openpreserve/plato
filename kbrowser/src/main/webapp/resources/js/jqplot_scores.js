var evaluatedValuesSeriesColors = [ "#FA5858", "#FA8258", "#F7D358", "#F4FA58",
		"#ACFA58", "#82FA58" ];

function plotDistributionOfScoresPie(targetId, data) {
	var values = [ [ '0', data['scores0'] ], [ '0.01-1', data['scores1'] ],
			[ '1.01-2', data['scores2'] ], [ '2.01-3', data['scores3'] ],
			[ '3.01-4', data['scores4'] ], [ '4.01-5', data['scores5'] ] ];

	$.jqplot(targetId, [ values ], {
		title : 'Distribution (%)',
		seriesColors : evaluatedValuesSeriesColors,
		seriesDefaults : {
			renderer : jQuery.jqplot.PieRenderer,
			rendererOptions : {
				showDataLabels : true,
				highlightMouseOver : false
			},
			shadow : false
		},
		legend : {
			show : true,
			location : 'ne'
		},
		axes : {
			xaxis : {
				showTicks : false
			}
		}
	}).replot();;

}

function plotDistributionOfScoresBar(targetId, data) {
	var values = new Array();

	// Set values
	values.push([ data['scores0'] ]);
	values.push([ data['scores1'] ]);
	values.push([ data['scores2'] ]);
	values.push([ data['scores3'] ]);
	values.push([ data['scores4'] ]);
	values.push([ data['scores5'] ]);

	// Plot diagram
	$.jqplot(targetId, values, {
		title : 'Distribution (absolute)',
		seriesColors : evaluatedValuesSeriesColors,
		seriesDefaults : {
			renderer : $.jqplot.BarRenderer,
			rendererOptions : {
				highlightMouseOver : false
			},
			pointLabels : {
				show : true,
				formatString : '%d',
				hideZeros : true
			},
			shadow : false
		},
		series : [ {
			label : '0'
		}, {
			label : '0.01-1'
		}, {
			label : '1.01-2'
		}, {
			label : '2.01-3'
		}, {
			label : '3.01-4'
		}, {
			label : '4.01-5'
		} ],
		legend : {
			show : true,
			placement : "inside"
		},
		axes : {
			xaxis : {
				renderer : $.jqplot.CategoryAxisRenderer,
				ticks : [ ' ' ]
			}
		}
	}).replot();;
}

function plotTransformerValues(targetID, values, transformerIDs, unit) {

	// Set values
	var convertedTransformers = new Array(values.length);
	for ( var i = 0; i < values.length; i++) {
		var convertedTransformerPoints = new Array(values[i].length);
		for ( var j = 0; j < values[i].length; j++) {

			var convertedDataPoint = new Array(2);
			convertedDataPoint[0] = values[i][j].x;
			convertedDataPoint[1] = values[i][j].y;
			convertedTransformerPoints[j] = convertedDataPoint;
		}
		convertedTransformers[i] = convertedTransformerPoints;
	}

	// Set series labels
	var seriesOptions = new Array(transformerIDs.length);
	for ( var i = 0; i < transformerIDs.length; i++) {
		seriesOptions[i] = {
			label : transformerIDs[i] + " " + (1 + i)
		};
	}

	var targetDiv = document.getElementById(targetID);
	
	var unitText = '';
	
	if (unit.length > 0) {
		unitText = ' (' + unit + ')';
	}

	// Plot diagram
	$.jqplot(targetID, convertedTransformers, {
		title : 'Utility Functions',
		sortData : false,
		legend : {
			show : true,
			placement : "inside"
		},
		seriesDefaults : {
			shadow : false
		},
		series : seriesOptions,
		axes : {
			xaxis : {
				label : "Measured Values" + unitText,
				pad : 0
			},
			yaxis : {
				label : "Scores",
				pad : 0,
				labelRenderer : $.jqplot.CanvasAxisLabelRenderer,
				labelOptions : {
					angle : -90,
					fontSize : '1em',
					textColor : targetDiv.style.color
				}
			},
		},
		
	}).replot();;
}

/*
 * function plotPotentialToRange(target, title, values) { var barValues = new
 * Array(); var seriesOptions = new Array();;
 * 
 * for (key in values) { barValues.push([values[key]]);
 * seriesOptions.push({label: key}); }
 * 
 * $.jqplot(target, barValues, { title : title, seriesDefaults : { renderer :
 * $.jqplot.BarRenderer, rendererOptions : { highlightMouseOver: false },
 * pointLabels : { show : true, formatString : "%.3f" } }, series :
 * seriesOptions, legend : { show : true, placement : "inside" }, axes : { xaxis : {
 * renderer : $.jqplot.CategoryAxisRenderer, ticks : [ ' ' ] } } }); }
 */