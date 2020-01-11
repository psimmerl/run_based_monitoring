import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl = new GraphErrors('Mean')
grtl.setTitle("CTOF time")
grtl.setTitleY("CTOF time (ns)")
grtl.setTitleX("run number")

def grtl2 = new GraphErrors('Sigma')
grtl2.setTitle("CTOF time")
grtl2.setTitleY("CTOF time (ns)")
grtl2.setTitleX("run number")

TDirectory out = new TDirectory()

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()
  def h1 = dir.getObject('/ctof/H_CVT_t_p50')

  // def f1 = ROOTFitter.fit(h1)

  //grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
  // grtl[it].addPoint(run, f1.getParameter(1), 0, 0)
  // grtl2[it].addPoint(run, f1.getParameter(2), 0, 0)
  grtl.addPoint(run, h1.getMean(), 0, 0)
  grtl2.addPoint(run, h1.getRMS(), 0, 0)
  // out.addDataSet(f1)

  out.mkdir('/'+run)
  out.cd('/'+run)
  out.addDataSet(h1)
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
grtl2.each{ out.addDataSet(it) }
out.writeFile('CTOF_time_all.hipo')
