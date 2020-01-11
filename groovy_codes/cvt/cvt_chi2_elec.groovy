import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors


def grtl = new GraphErrors('cvt_chi2_elec')
grtl.setTitle("Average CVT chi2, electrons")
grtl.setTitleY("Average CVT chi2, electrons")
grtl.setTitleX("run number")

TDirectory out = new TDirectory()

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()

  def h1 = dir.getObject('/cvt/H_CVT_chi2')

  grtl.addPoint(run, h1.getMean(), 0, 0)

  out.mkdir('/'+run)
  out.cd('/'+run)
  out.addDataSet(h1)
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('cvt_chi2_elec.hipo')
