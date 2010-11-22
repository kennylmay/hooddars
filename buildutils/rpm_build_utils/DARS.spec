Name: DARS
Version: 1.0.0
Release: 2
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

%build

%install
SRCDIR=%{_topdir}/SOURCES/%{name}
EXECDIR=${RPM_BUILD_ROOT}/usr/bin
JARDIR=${RPM_BUILD_ROOT}/usr/lib/DARS

rm -rf $RPM_BUILD_ROOT
mkdir -p $EXECDIR
mkdir -p $JARDIR

install -m 755 $SRCDIR/DARS $EXECDIR/
install -m 640 $SRCDIR/DARS.jar $JARDIR/


%files
%defattr(755,root,root)
/usr/bin/DARS

%defattr(-,root,root)
/usr/lib/DARS/DARS.jar

%pre

%clean
rm -rf $RPM_BUILD_ROOT
rm -rf %{_sourcedir}/%{name}

%post

%changelog
* Sun Nov 21 2010 Kenny May <kennylmay@gmail.com>
- DARS-1.0.0-1: Initial Version
