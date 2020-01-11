import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl = new GraphErrors('CVT pathlength')
grtl.setTitle("CVT pathlength")
grtl.setTitleY("CVT pathlength (cm)")
grtl.setTitleX("run number")


TDirectory out = new TDirectory()

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()


    // def h2 = dir.getObject('/elec/H_trig_vz_mom_S'+(it+1))
    // def h1 = h2.projectionY()
    def h1 = dir.getObject('/cvt/hpathlen')
    h1.setTitle("CVT pathlength");
		h1.setTitleX("CVT pathlength (cm)");

    grtl.addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)

    out.mkdir('/'+run)
    out.cd('/'+run)
    out.addDataSet(h1)
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('cvt_pathlen.hipo')
