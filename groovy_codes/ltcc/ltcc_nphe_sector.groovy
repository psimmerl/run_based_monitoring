import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
// import ROOTFitter

def grtl = (0..1).collect{
  sec_num=2*it+3
  def gr = new GraphErrors('sec'+sec_num)
  gr.setTitle("LTCC Number of Photoelectrons for electrons")
  gr.setTitleY("LTCC Number of Photoelectrons for electrons per sector")
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

  out.mkdir('/'+run)
  out.cd('/'+run)

  (0..1).each{
    sec_num=2*it+3
    def h2 = dir.getObject('/elec/H_trig_LTCCn_theta_S'+(sec_num))
    def h1 = h2.projectionY()
    h1.setName("sec"+(sec_num))
    h1.setTitle("LTCC Number of Photoelectrons for elec")
    h1.setTitleX("LTCC Number of Photoelectrons for elec")

    // def f1 = ROOTFitter.fit(h1)

    // grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
    // grtl[it].addPoint(run, f1.getParameter(1), 0, 0)
    grtl[it].addPoint(run, h1.getMean(), 0, 0)
    out.addDataSet(h1)
    // out.addDataSet(f1)
  }
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('ltcc_elec_nphe_sec.hipo')
