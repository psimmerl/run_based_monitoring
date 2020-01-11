import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl = new GraphErrors('CVT transverse momentum')
grtl.setTitle("CVT transverse momentum")
grtl.setTitleY("CVT transverse momentum (GeV/c)")
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
    def h1 = dir.getObject('/cvt/hpt')
    h1.setTitle("CVT track transverse momentum");
		h1.setTitleX("CVT track transverse momentum (GeV/c)");

    // def f1 = new F1D("fit:"+h1.getName(), "[amp]*gaus(x,[mean],[sigma])", 0,10);
    // f1.setLineWidth(2);
    // f1.setOptStat("1111");
    // initTimeGaussFitPar(f1,h1);
    // DataFitter.fit(f1,h1,"LQ");

    grtl.addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)

    out.mkdir('/'+run)
    out.cd('/'+run)
    out.addDataSet(h1)
    // out.addDataSet(f1)
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('cvt_pt.hipo')
