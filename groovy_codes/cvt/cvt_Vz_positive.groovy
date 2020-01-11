import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;


def grtl = new GraphErrors('cvt_z_pos')
grtl.setTitle("VZ (Average), positives")
grtl.setTitleY("VZ (Average), positives (cm)")
// grtl.setTitle("VZ (peak value), positives")
// grtl.setTitleY("VZ (peak value), positives")
grtl.setTitleX("run number")

TDirectory out = new TDirectory()

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()

  def h1 = dir.getObject('/cvt/H_CVT_z_pos')
  grtl.addPoint(run, h1.getMean(), 0, 0)
  // grtl.addPoint(run, f1.getParameter(1), 0, 0)


  out.mkdir('/'+run)
  out.cd('/'+run)
  out.addDataSet(h1)
  // out.addDataSet(f1)
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('cvt_Vz_pos.hipo')
