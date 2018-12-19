import csv
from bokeh.io import show, output_file
from bokeh.models import ColumnDataSource, FactorRange
from bokeh.plotting import figure
from bokeh.io import export_png

segments = [5, 10, 15, 20, 25, 30]
metrics = ["C", "P"]
content = []
x = [(str(segment), metric) for segment in segments for metric in metrics]

datafiles = ["../data/results5000.dat", "../data/results10000.dat", "../data/results15000.dat", "../data/results20000.dat"]

i = 0
for file in datafiles:
	with open(file, mode='r') as csv_file:
		csv_reader = csv.DictReader(csv_file)

		cost = []
		performance = []
		for row in csv_reader:
			cost.append(row['cost'])
			performance.append(row['performance'])

		data = {'segmentsCount' : segments,
				'cost' : cost,
				'performance' : performance}
		counts = sum(zip(data['cost'], data['performance']), ())
		source = ColumnDataSource(data=dict(x=x, counts=counts))

		title = ("Length " + file[12:16]) if i == 0 else ("Length " + file[12:17])
		p = figure(x_range=FactorRange(*x), plot_height=250, title=title,
        	toolbar_location=None, tools="")
		p.vbar(x='x', top='counts', width=0.9, source=source)

		show(p)
		p = None
	i += 1