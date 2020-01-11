import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
// import ROOTFitter

def grtl = (1..48).collect{
  sec_num=(it-1).intdiv(8)+1
  remainder=(it-1)%8
  ring_num = (remainder)%4+1
  side_num = (remainder).intdiv(4)+1
  def gr = new GraphErrors('sec'+sec_num+' ring'+ring_num+' side'+side_num)
  gr.setTitle("HTCC vtime - STT, electrons")
  gr.setTitleY("HTCC vtime - STT, electrons, per PMTs (ns)")
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
    for (side=0; side<2; side++){
      int counter = r + 4*( side + 2*s );
      def h1 = dir.getObject(String.format("/HTCC/H_HTCC_vtime_s%d_r%d_side%d",s+1,r+1,side+1))//left
      h1.setName("sec"+(s+1) +"ring"+(r+1)+"side"+(side+1))

      // def f1 = ROOTFitter.fit(h1)

      // grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
      // grtl[counter].addPoint(run, f1.getParameter(1), 0, 0)
      grtl[counter].addPoint(run, h1.getMean(), 0, 0)
      out.addDataSet(h1)
      // out.addDataSet(f1)
    }
  }
}
}

out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('htcc_vtimediff.hipo')
