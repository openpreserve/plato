(define (migratePNGtoTIFF infile outfile compression)
	(let* ((image (car (file-png-load 1 infile infile)))
	  (drawable (car (gimp-image-active-drawable image)))
	 )
	 	 
	 ; flatten image if it has an alpha channel
	 (if (gimp-drawable-has-alpha drawable)
		(set! drawable (car (gimp-image-flatten image)))
     )
	 
	 ; file-tiff-save (Saves files in tiff file format)
	 (file-tiff-save
		 1            ;   run-mode     INT32     Interactive, non-interactive
		 image        ;   image        IMAGE     Input image
		 drawable     ;   drawable     DRAWABLE  Drawable to save
		 outfile      ;   filename     STRING    file name to save
		 outfile      ;   raw-filename STRING    file name to save
		 compression  ;   compression  INT32     Compression type: {None (0), LZW (1), PACKBITS(2), DEFLATE (3), PNG (4), CCITT G3 Fax (5), CCITT G4 Fax (6)}
		 )
	)
)

(script-fu-register
   "migratePNGtoTIFF"
   "<Toolbox>/Xtns/Script-Fu/miniMEE/migratePNGtoTIFF" 
   "Migrate PNG to TIFF"
   "MINIMEE"
   "Copyright 2008 by miniIMEE"
   "2009-03-03"
   ""
   SF-FILENAME "Infile"      "infile.png"
   SF-FILENAME "Outfile"     "outfile.tiff"
   SF-VALUE    "Compression" "1"
)
