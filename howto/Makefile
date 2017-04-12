
recommender-howto.pdf:  recommender-howto.tex kunegis.bib
	rm -f *.aux *.log *.out
	pdflatex recommender-howto.tex
	bibtex recommender-howto
	pdflatex recommender-howto.tex
	pdflatex recommender-howto.tex

kunegis.bib:  ../../bibtex/kunegis.bib
	cp ../../bibtex/kunegis.bib .
