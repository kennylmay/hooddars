Name: DARS
Version: 1.0.0
Release: 4
Source0: http://hooddars.googlecode.com/svn/trunk/
Distribution: Red hat Linux
URL: http://hooddars.googlecode.com/svn/trunk/
Packager: Kenny May <kennylmay@gmail.com>
License: US Government Proprietary
Summary: Dynamic Ad-Hoc routing simulator
Exclusiveos: linux
Buildroot: %{_topdir}/BUILD/%{name}
Buildarch: noarch

%description
This package will install the DARS simulator.

%prep
#Define BUILDDIR
BUILDDIR=/tmp/DARS
#Clean the temp build area
if [ -e ${BUILDDIR} ];then
  rm -rf ${BUILDDIR}
fi

#Check the current code out
svn checkout http://hooddars.googlecode.com/svn/trunk/ ${BUILDDIR}

%build
#Define BUILDDIR and SRCDIR
BUILDDIR=/tmp/DARS
SRCDIR=%{_topdir}/SOURCES/%{name}

#Change to that directory
cd ${BUILDDIR}/src

#Compile all java files
find -iname *.java | xargs javac -d ../bin

#Change to the bin directory
cd ${BUILDDIR}/bin

#Create manifest file
echo "Main-Class: dars.DARSMain" >> manifest

#Copy our images to the bin directory
cp ${BUILDDIR}/img/* ${BUILDDIR}/bin/

#Create the jar file
cd ${BUILDDIR}/bin
find -iname *.class | xargs jar cfm DARSApp.jar manifest *.png

cp ${BUILDDIR}/bin/DARSApp.jar $SRCDIR/

%install
SRCDIR=%{_topdir}/SOURCES/%{name}
EXECDIR=${RPM_BUILD_ROOT}/usr/bin
JARDIR=${RPM_BUILD_ROOT}/usr/lib/DARS

rm -rf $RPM_BUILD_ROOT
mkdir -p $EXECDIR
mkdir -p $JARDIR

install -m 755 $SRCDIR/DARS $EXECDIR/
install -m 640 $SRCDIR/DARSApp.jar $JARDIR/


%files
%defattr(755,root,root)
/usr/bin/DARS

%defattr(-,root,root)
/usr/lib/DARS/DARSApp.jar

%pre

%clean
rm -rf $RPM_BUILD_ROOT
rm -rf %{_sourcedir}/%{name}
rm -rf /tmp/DARS

%post

%changelog
* Sun Nov 21 2010 Kenny May <kennylmay@gmail.com>
- DARS-1.0.0-1: Initial Version
