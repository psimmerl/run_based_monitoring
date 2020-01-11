import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
// import ROOTFitter

def grtl = (1..24).collect{
  sec_num=(it-1).intdiv(4)+1
  ring_num=(it-1)%4 +1
  def gr = new GraphErrors('sec'+sec_num+' ring'+ring_num)
  gr.setTitle("HTCC Number of Photoelectrons")
  gr.setTitleY("HTCC Number of Photoelectrons per sector per ring")
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

for (s = 0; s <6; s++) {
  for (r = 0; r <4; r++) {

    int counter = r + 4*s
    def h1 = dir.getObject(String.format('/HTCC/H_HTCC_nphe_s%d_r%d_side1',s+1,r+1)) //left
    def h2 = dir.getObject(String.format('/HTCC/H_HTCC_nphe_s%d_r%d_side2',s+1,r+1)) //right
    h1.add(h2)
    h1.setName("sec"+(s+1) +"ring"+(r+1))
    h1.setTitle("HTCC Number of Photoelectrons")
    h1.setTitleX("HTCC Number of Photoelectrons")

    // def f1 = ROOTFitter.fit(h1)

    // grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
    // grtl[counter].addPoint(run, f1.getParameter(1), 0, 0)
    grtl[counter].addPoint(run, h1.getMean(), 0, 0)
    out.addDataSet(h1)
    // out.addDataSet(f1)
  }
}
}

out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('htcc_nphe_sec_ring.hipo')
