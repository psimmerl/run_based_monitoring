import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import FTFitter;

data = []

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()

  def h1 = dir.getObject('/ft/hpi0sum')
  def f1 = FTFitter.pi0fit(h1)

  data.add([run:run, h1:h1, f1:f1, mean:f1.getParameter(1), sigma:f1.getParameter(2).abs(), chi2:f1.getChiSquare()])
}


['mean', 'sigma'].each{name->
  def grtl = new GraphErrors(name)
  grtl.setTitle("FTC pi0 mass")
  grtl.setTitleY("FTC pi0 mass ("+name+" value) (MeV)")
  grtl.setTitleX("run number")

  TDirectory out = new TDirectory()

  data.each{
    out.mkdir('/'+it.run)
    out.cd('/'+it.run)
    out.addDataSet(it.h1)
    out.addDataSet(it.f1)
    grtl.addPoint(it.run, it[name], 0, 0)
  }

  out.mkdir('/timelines')
  out.cd('/timelines')
  out.addDataSet(grtl)
  out.writeFile('ftc_pi0_mass_'+name+'.hipo')
}
