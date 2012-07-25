This is the selenium test for a bug reported in https://github.com/openplanets/plato/issues/53
It depends on a jpg file (ikea1.jpg) that is uploaded from the client (hard-coded path in the selenium test needs to be adapted).


The test creates an FTE evaluation for ImageIO (standard in Minimee) and runs it, assuming it runs successfuly.
It then tries to compare the output file (fileMatrix). It fails since this currently produces an error. 