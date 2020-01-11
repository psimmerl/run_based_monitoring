import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
// import ROOTFitter

def grtl = (1..2).collect{
  def gr = new GraphErrors('Comb'+it)
  gr.setTitle("BAND mean time fadc sector combination")
  gr.setTitleY("meantimeFadc – sqrt(x^2+y^2+z^2)/c (ns)")
  gr.setTitleX("run number")
  return gr
}


TDirectory out = new TDirectory()

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()

    // def h2 = dir.getObject('/elec/H_trig_vz_mom_S'+(it+1))
    // def h1 = h2.projectionY()
    def h1 = dir.getObject('/BAND/H_BAND_MeanTimeFADC_SectorCombination1')
    def h2 = dir.getObject('/BAND/H_BAND_MeanTimeFADC_SectorCombination2')

    grtl[0].addPoint(run, h1.getMean(), 0, 0)
    grtl[1].addPoint(run, h2.getMean(), 0, 0)
    // grtl[it].addPoint(run, f1.getParameter(1), 0, 0)
    out.mkdir('/'+run)
    out.cd('/'+run)
    out.addDataSet(h1)
    out.addDataSet(h2)
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('band_meantimeadc.hipo')
