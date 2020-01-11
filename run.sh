#!/usr/bin/bash

#Enviroment initialization
#for output coordinating
#Change the location of groovy accordingly.
$SECONDS=0
verbose=false
file_list="files.list"
arg_counter=0
threads=1
skip_next_arg=false
for arg in "$@"
do	
	arg_counter=$((arg_counter + 1))
	if [ "$arg" == "-h" ] || [ "$arg" == "--help" ] 
	then
		echo "Options:"
		echo -e "-h\t: Prints this message (also --help)."
		echo -e "-v\t: Prints output with maximum verbosity (default is only  warnings and errors)."
		echo -e "-f\t: Allows user to define the input file list (default file is files.list)."
		echo -e "-p\t: Allows user to define the number of scripts that can be run in parallel (default is one thread)." 

		echo -e "\nA full log with the complete groovy script output is saved to log.out"
		exit
	elif [ "$arg" == "-v" ] 
	then
		verbose=true
	elif [ "$arg" == "-f" ] 
	then
		next=$((arg_counter+1)); file_list="${!next}"
		skip_next_arg=true
	elif [ "$arg" == "-p" ] 
	then
		next=$((arg_counter+1)); threads="${!next}"
		skip_next_arg=true
	elif ! $skip_next_arg
	then
		echo "Command $arg not recognized. For help use -h."
		exit
	else
		skip_next_arg=false
	fi		
done
echo "Threads:   $threads"
echo "File List: $file_list"
echo "Verbose:   $verbose"
#export JYPATH=~/psimmerl/run_based_monitoring/groovy_codes/rf/:~/psimmerl/run_based_monitoring/groovy_codes/ft/
export JYPATH=/home/kenjo/clas12/run_based_monitoring/groovy_codes/rf/:/home/kenjo/clas12/run_based_monitoring/groovy_codes/ft/


export groovy=run-groovy
#Output directory names
mkdir -p rga_pass0
cd rga_pass0

LOG=../log.out
echo > $LOG

#subdirectory names
#bmtbst central cnd ctof cvt dc ec forward ft ftof htcc ltcc rf trigger
out_dir=(band bmtbst central cnd ctof cvt dc ec forward ft ftof htcc ltcc rf trigger particle_mass_ctof_and_ftof)
dir_array=out_dir[@]
for dir in ${!dir_array}
do
        mkdir -p "$dir"
done

#hipo used for each detectors.
#bmtbst - out_monitor
#central - out_monitor
#cnd - out_CND
#ctof - out_CTOF
#cvt - out_monitor
#dc - out_TOF
#ec - out_monitor
#ft - out_FTOF
#forward - out_monitor
#rf - out_monitor
#trigger - out_monitor
#ftof - out_TOF, dst_mon
#htcc - out_HTCC, out_monitor
#ltcc - out_monitor, out_LTCC

out_monitor=(bmtbst/bmt_Occupancy bmtbst/bmt_OnTrkLayers bmtbst/bst_Occupancy bmtbst/bst_OnTrkLayers \
central/central_Km_num central/central_pim_num central/central_prot_num central/central_Kp_num central/central_pip_num \
cvt/cvt_Vz_negative cvt/cvt_Vz_positive cvt/cvt_chi2_elec cvt/cvt_chi2_neg cvt/cvt_chi2_pos cvt/cvt_chi2norm \
cvt/cvt_ndf cvt/cvt_p cvt/cvt_pathlen cvt/cvt_pt cvt/cvt_trks cvt/cvt_trks_neg cvt/cvt_trks_neg_rat cvt/cvt_trks_pos cvt/cvt_trks_pos_rat 
trigger/rat_Km_num trigger/rat_neg_num trigger/rat_pos_num trigger/rat_Kp_num \
trigger/rat_neu_num trigger/rat_prot_num trigger/rat_elec_num trigger/rat_pim_num trigger/rat_muon_num trigger/rat_pip_num \
forward/forward_Tracking_Elechi2 forward/forward_Tracking_EleVz forward/forward_Tracking_Poschi2 forward/forward_Tracking_PosVz \
forward/forward_Tracking_Negchi2 forward/forward_Tracking_NegVz \
ec/ec_Sampl ec/ec_gg_m ec/ec_pip_time ec/ec_pim_time \
htcc/htcc_nphe_sector \
ltcc/ltcc_nphe_sector \
rf/rftime_diff rf/rftime_pim_FD rf/rftime_pim_CD rf/rftime_pip_FD rf/rftime_pip_CD \
rf/rftime_elec_FD rf/rftime_elec_CD rf/rftime_prot_FD rf/rftime_prot_CD)
out_CND=(cnd/cnd_MIPS_dE_dz cnd/cnd_time_neg_vtP cnd/cnd_zdiff)
out_CTOF=(ctof/ctof_edep ctof/ctof_time particle_mass_ctof_and_ftof/ctof_m2_pim particle_mass_ctof_and_ftof/ctof_m2_pip)
out_CTOF=(ctof/ctof_edep ctof/ctof_time)
out_FT=(ft/ftc_pi0_mass ft/ftc_time_charged ft/ftc_time_neutral ft/fth_MIPS_energy ft/fth_MIPS_time ft/fth_MIPS_energy_board ft/fth_MIPS_time_board)
dst_mon=(particle_mass_ctof_and_ftof/ftof_m2_p1a_pim particle_mass_ctof_and_ftof/ftof_m2_p1a_pip particle_mass_ctof_and_ftof/ftof_m2_p1a_prot particle_mass_ctof_and_ftof/ftof_m2_p1b_pim particle_mass_ctof_and_ftof/ftof_m2_p1b_pip particle_mass_ctof_and_ftof/ftof_m2_p1b_prot)
out_HTCC=(htcc/htcc_nphe_ring_sector)
out_LTCC=(ltcc/ltcc_had_nphe_sector)
out_BAND=(band/band_adccor band/band_meantimeadc band/band_meantimetdc)
out_TOF=(ftof/ftof_edep_p1a_smallangles ftof/ftof_edep_p1a_midangles ftof/ftof_edep_p1a_largeangles ftof/ftof_edep_p1b_smallangles ftof/ftof_edep_p1b_midangles ftof/ftof_edep_p1b_largeangles ftof/ftof_edep_p2 \
ftof/ftof_time_p1a ftof/ftof_time_p1b ftof/ftof_time_p2 \
dc/dc_residuals_sec dc/dc_residuals_sec_sl dc/dc_t0_sec_sl  dc/dc_tmax_sec_sl)
#out_TOF=(ftof/ftof_edep_p1a_smallangles ftof/ftof_edep_p1a_midangles ftof/ftof_edep_p1a_largeangles ftof/ftof_edep_p1b_smallangles ftof/ftof_edep_p1b_midangles ftof/ftof_edep_p1b_largeangles ftof/ftof_edep_p2)
#out_TOF=(ftof/ftof_time_p1a ftof/ftof_time_p1b ftof/ftof_time_p2)
#out_TOF=(dc/dc_residuals_sec dc/dc_residuals_sec_sl dc/dc_tmax_sec_sl)

full_list=""

for name in out_monitor out_CND out_TOF out_CTOF out_FT out_HTCC out_LTCC out_BAND
#for name in out_monitor
#for name in out_FT
do 
	file_names=$(echo `grep "$name" ../$file_list`)
	var=$name[@]
	for script in ${!var}
	do	
		#list_per_script=$(echo "$file_names" | xargs -I {} echo "$groovy ../groovy_codes/$script.groovy {}")

#Building full list of commands for each groovy script and its corresponding files
		full_list+="
$script $file_names"
	


		#echo $script
		#output=$(grep "$name" ../files.list | tr ' ' '\n' | xargs -I {} -P 16 sh -c "$groovy ../groovy_codes/$script.groovy {}")
        	#echo "$output" | grep -i -E  -- "error|warning"
		
		#if [ "$script" == "particle_mass_ctof_and_ftof/ftof_m2_p1b_prot" ];then
		#	echo $script
		#	$groovy ../groovy_codes/$script.groovy `find /work/clas12/rg-b/offline_monitoring/plots* -name "$name*" ! -name "*6582*"`
		#else
		# $groovy ../groovy_codes/$script.groovy `find /work/clas12/rg-a/software/clas12_monitoring/plots* -name "set0*_$name*.hipo"`
		
		#$groovy ../groovy_codes/$script.groovy `grep "$name" ../files.list`
		#echo "$output">>$LOG
		#fi

	done
done
echo -e "\nScript List Generated\nBeginning Groovy Processes\n"
#echo "$full_list"

if $verbose
then
	echo "$full_list" | xargs -L 1 -P $threads --process-slot-var=index bash -c 'echo -e "\nScript: $1 \nFile: ${@:2} \nProcess-id: $$ \nCore-id: $index" |& tee -i "$1.out"; $groovy ../groovy_codes/$1.groovy ${@:2} |& tee -i -a "$1.out"; sed -i "1i$1 $(($SECONDS/3600)):$(($SECONDS%3600/60)):$(($SECONDS%60))" "$1.out";' sh | tee $LOG

else
	echo "$full_list" | xargs -L 1 -P $threads --process-slot-var=index bash -c 'echo -e "\nScript: $1 \nFile: ${@:2} \nProcess-id: $$ \nCore-id: $index" |& tee -i "$1.out"; $groovy ../groovy_codes/$1.groovy ${@:2} |& tee -i -a "$1.out"; sed -i "1i$1 $(($SECONDS/3600)):$(($SECONDS%3600/60)):$(($SECONDS%60))" "$1.out";' sh | grep -i -E -- "Script:|Core-id:|file :|ERROR|WARNING" #caught|fail|failed|failure|exception"

fi

logs=$(find -type f -name "*.out")
for file in $logs
do
	cat $file >> $LOG
	sed -i "1i$(head -n 1 $file)" $LOG
	rm $file
done

mv bmt_*.hipo bmtbst/
mv bst_*.hipo bmtbst/
mv cen_*.hipo central/
mv cnd_*.hipo cnd/
mv ctof_*.hipo ctof/
mv cvt_*.hipo cvt/
mv dc_*.hipo dc/
mv ec_*.hipo ec/
mv forward_*.hipo forward/
mv ftc_*.hipo ft/
mv fth_*.hipo ft/
mv ftof_*.hipo ftof/
mv htcc_*.hipo htcc/
mv ltcc_*.hipo ltcc/
mv rftime_*.hipo rf/
mv rat_* trigger/
mv ctof/*m2* particle_mass_ctof_and_ftof/
mv ftof/*m2* particle_mass_ctof_and_ftof
mv band_* band/

elapsed="$(($SECONDS/3600)):$(($SECONDS%3600/60)):$(($SECONDS%60))"

echo "Total Time $elapsed"
sed -i "1iTotal Time $elapsed" $LOG

cd ..
cp -r rga_pass0 /group/clas/www/clas12mon/html/hipo/paul

