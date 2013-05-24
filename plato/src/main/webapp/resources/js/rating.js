$.fn.convertRating = function() {
	return $(this)
			.each(
					function() {
						// Get the value
						var val = parseFloat($(this).html());
						// Make sure that the value is in 0 - 5 range, multiply
						// to get width
						var $emptyStars = $('<span>&#9734;&#9734;&#9734;&#9734;&#9734;</span>');
						$(this).html($emptyStars);
						var size = Math.max(0, (Math.min(5, val)))
								* $emptyStars.innerWidth() / 5;
						var $fullStars = $(
								'<span>&#9733;&#9733;&#9733;&#9733;&#9733;</span>')
								.width(size);

						// Replace the numerical value with stars
						$(this).append($fullStars);
					});
};