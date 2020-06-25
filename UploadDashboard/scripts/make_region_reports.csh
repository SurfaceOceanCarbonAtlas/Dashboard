#! /bin/csh
# A	North Atlantic
# C	Coastal
# G	Global
# I	Indian
# N	North Pacific
# O	Southern Ocean
# R	Arctic
# T	Tropical Pacific
# Z	Tropical Atlantic
../scripts/mkreports.sh FlagABCD_expos.txt multi/SOCATv2020_Indian.tsv           I  >&! multi/indian_report.log
../scripts/mkreports.sh FlagABCD_expos.txt multi/SOCATv2020_NorthPacific.tsv     N  >&! multi/northpac_report.log
../scripts/mkreports.sh FlagABCD_expos.txt multi/SOCATv2020_TropicalAtlantic.tsv Z  >&! multi/tropatl_report.log
../scripts/mkreports.sh FlagE_Expos.txt    multi/SOCATv2020_FlagE.tsv            '' >&! multi/flage_report.log
../scripts/mkreports.sh FlagABCD_expos.txt multi/SOCATv2020_Arctic.tsv           R  >&! multi/arctic_report.log
../scripts/mkreports.sh FlagABCD_expos.txt multi/SOCATv2020_NorthAtlantic.tsv    A  >&! multi/northatl_report.log
../scripts/mkreports.sh FlagABCD_expos.txt multi/SOCATv2020_TropicalPacific.tsv  T  >&! multi/troppac_report.log
../scripts/mkreports.sh FlagABCD_expos.txt multi/SOCATv2020_SouthernOceans.tsv   O  >&! multi/southern_report.log
../scripts/mkreports.sh FlagABCD_expos.txt multi/SOCATv2020_Coastal.tsv          C  >&! multi/coastal_report.log
../scripts/mkreports.sh FlagABCD_expos.txt multi/SOCATv2020.tsv                  '' >&! multi/global_report.log
