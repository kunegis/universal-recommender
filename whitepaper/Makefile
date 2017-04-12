#
# Whitepaper:  The Universal Recommender
#
# https://wiki.dai-labor.de/The_Universal_Recommender
#

all:  unirec.pdf

unirec.pdf unirec.bbl:  unirec.tex kunegis.bib iptv-er.pdf overview-crop.pdf 
	pdflatex unirec
	bibtex unirec
	pdflatex unirec
	pdflatex unirec

kunegis.bib:  ../../bibtex/kunegis.bib
	cp $< $@

%-crop.pdf:  %.pdf
	pdfcrop $*

unirec.tar:  unirec.tex unirec.bbl
	tar cfvv unirec.tar bibspacing.sty citesort.sty iptv-er.pdf unirec.bbl overview-crop.pdf unirec.tex
