import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
// import ROOTFitter

def grtl = (0..1).collect{
  sec_num=2*it+3
  def gr = new GraphErrors('sec'+sec_num)
  gr.setTitle("LTCC Number of Photoelectrons for piplus")
  gr.setTitleY("LTCC Number of Photoelectrons for piplus per sector")
  gr.setTitleX("run number")
  return gr
}

def grtl2 = (0..1).collect{
  sec_num=2*it+3
  def gr = new GraphErrors('sec'+sec_num)
  gr.setTitle("LTCC Number of Photoelectrons for piminus")
  gr.setTitleY("LTCC Number of Photoelectrons for piminus per sector")
  gr.setTitleX("run number")
  return gr
}

TDirectory out = new TDirectory()
TDirectory out2 = new TDirectory()

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()

  out.mkdir('/'+run)
  out.cd('/'+run)
  out2.mkdir('/'+run)
  out2.cd('/'+run)

    def h1 = dir.getObject('/LTCC/H_piplus_S3_nphe')
    def h2 = dir.getObject('/LTCC/H_piplus_S5_nphe')
    grtl[0].addPoint(run, h1.getMean(), 0, 0)
    grtl[1].addPoint(run, h2.getMean(), 0, 0)
    out.addDataSet(h1)
    out.addDataSet(h2)

    def h3 = dir.getObject('/LTCC/H_piminus_S3_nphe')
    def h4 = dir.getObject('/LTCC/H_piminus_S5_nphe')
    grtl2[0].addPoint(run, h3.getMean(), 0, 0)
    grtl2[1].addPoint(run, h4.getMean(), 0, 0)
    out2.addDataSet(h3)
    out2.addDataSet(h4)

}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('ltcc_pip_nphe_sec.hipo')

out2.mkdir('/timelines')
out2.cd('/timelines')
grtl2.each{ out2.addDataSet(it) }
out2.writeFile('ltcc_pim_nphe_sec.hipo')
