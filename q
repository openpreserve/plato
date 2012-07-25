[33mcommit 85d62c13d6449a24bb804ae171302dcfdbb32503[m
Author: Michael Kraxner <kraxner@ifs.tuwien.ac.at>
Date:   Wed Jul 25 14:37:07 2012 +0200

    removed xcl files

[33mcommit 9d157348fb1d1b2230c38ccc92236800384f54b0[m
Author: Michael Kraxner <kraxner@ifs.tuwien.ac.at>
Date:   Wed Jul 25 14:30:07 2012 +0200

    added minimee tools, FITS_HOME must be set now
    
    * added additional files required to setup minimee, minimee/minimee-tools
      copy them to a directory of your choice and adjust miniMEE-tool-configs.xml
    * removed reading of fits.properties, now you MUST set environment var FITS_HOME to use it

[33mcommit 22e6e91cbfdb7b41b9b0ff9b49f88fede51a6142[m
Author: Petar Petrov <me@petarpetrov.org>
Date:   Wed Jul 25 09:57:52 2012 +0200

    adding step info in define samples
    
    adding an information div in define samples
    and fixing some css issues, so that the info
    is displayed next to the anchors.

[33mcommit 0c71a5db3a8cacb3c7372811355c205fe2d9e287[m
Author: Petar Petrov <me@petarpetrov.org>
Date:   Tue Jul 24 18:13:14 2012 +0200

    improving front-end
    
    adding better css for the text area and text fields
    improving the ugly trigger codes from the enumeration
    by exchanging their values on the client side via jquery

[33mcommit 13e80d1d24f7da8837b5e8f6d864ac2ce4638ef6[m
Merge: 729e1a1 2f91465
Author: Christoph Becker <becker@ifs.tuwien.ac.at>
Date:   Wed Jul 25 09:57:27 2012 +0200

    Merge branch 'integration' of github.com:openplanets/plato into integration

[33mcommit 729e1a1e8735918b05f459239356d24f964de341[m
Author: Christoph Becker <becker@ifs.tuwien.ac.at>
Date:   Wed Jul 25 09:49:48 2012 +0200

    fixed some html validation errors

[33mcommit 2f914653b217a5faa2bbeca56fc4bdd48b20ebc2[m
Author: Kresimir Duretec <kresimir.duretec@gmail.com>
Date:   Wed Jul 25 09:22:29 2012 +0200

    added arquillian to plato

[33mcommit d57d589431f1ff69d67b41da5527616b98f155e4[m
Author: Markus Plangg <plangg@ifs.tuwien.ac.at>
Date:   Tue Jul 24 11:51:21 2012 +0200

    Removed depth parsing of output ports

[33mcommit 434fa68b93356df04efc18e8cc9ab00f34966ab9[m
Author: Markus Plangg <plangg@ifs.tuwien.ac.at>
Date:   Mon Jul 23 19:01:30 2012 +0200

    Added T2Flow parser

[33mcommit dcaa802ed7ac180cd21e1b97f7fbceb41e103b09[m
Author: Kresimir Duretec <kresimir.duretec@gmail.com>
Date:   Thu Jul 19 13:03:41 2012 +0200

    ByteStreamManager test is working in JBoss container

[33mcommit 4ce00ad4640f91bcde1ac85d699bc5997ad20edb[m
Author: Kresimir Duretec <kresimir.duretec@gmail.com>
Date:   Fri Jul 13 17:05:46 2012 +0200

    some changes in ByteStreamManager, test is still not working

[33mcommit 872d5d898335f760d4f2b20c05462122b418161c[m
Author: Kresimir Duretec <kresimir.duretec@gmail.com>
Date:   Fri Jul 6 12:13:30 2012 +0200

    adding tests for ByteStreamManager fixes #38

[33mcommit dbc6feb86d79d10045cb8abf045e9551ae71ffc9[m
Author: Kresimir Duretec <kresimir.duretec@gmail.com>
Date:   Thu Jul 5 18:10:27 2012 +0200

    adding Arquillian to enable ByteStreamManager testing

[33mcommit 552b9eed074d1b70e7ab7886fbdbd82669ce4e1f[m
Author: Kresimir Duretec <kresimir.duretec@gmail.com>
Date:   Wed Jul 4 15:23:34 2012 +0200

    adding tests for the FileStorage and fixing the bug fixes #37

[33mcommit 2078f1da91f3115eb54400c0c97dcf138749c151[m
Author: Kresimir Duretec <kresimir.duretec@gmail.com>
Date:   Tue Jul 3 18:02:48 2012 +0200

    adding support for config file in FileStorage

[33mcommit caf5330624d8372760d773ef40fe39af91333ed8[m
Author: Kresimir Duretec <kresimir.duretec@gmail.com>
Date:   Mon Jul 2 15:12:01 2012 +0200

    changing comments in DigitalObjectManager fixes #34

[33mcommit 7348cbc5803da13e14a1fdc583b6c9e476eed5d0[m
Author: Michael Kraxner <kraxner@ifs.tuwien.ac.at>
Date:   Thu Jun 28 11:35:35 2012 +0200

    plato-model: added project mvn repository
    
    added a project maven repository for plato-model
    it provides flanagan.jar, the root for this repo is plato-model/lib
    fixes #39

[33mcommit d175b729a05028c27f9dac280cecd59aef4f20b1[m
Author: Kresimir Duretec <kresimir.duretec@gmail.com>
Date:   Tue Jun 26 17:46:34 2012 +0200

    adding cache support to the ByteStreamManager fixes #35

[33mcommit 32a04485bf605a31b249b18db1f2d2d169d34f0b[m
Author: Kresimir Duretec <kresimir.duretec@gmail.com>
Date:   Tue Jun 26 14:34:01 2012 +0200

    removing DigitalObject from the ByteStreamManager fixes #36

[33mcommit 21032636ae8907e80dde33f4b96789b3685f2971[m
Author: Kresimir Duretec <kresimir.duretec@gmail.com>
Date:   Mon Jun 25 17:19:36 2012 +0200

    adding a test for Measurements

[33mcommit 03e2b4490dc40ab1cebbd5b6cc4dcf049af1d80c[m
Author: Kresimir Duretec <kresimir.duretec@gmail.com>
Date:   Mon Jun 25 11:59:33 2012 +0200

    adding tests for Transformers

[33mcommit bd2924dc8aa9951658ba46164ede980ad68f3906[m
Author: Kresimir Duretec <kresimir.duretec@gmail.com>
Date:   Fri Jun 22 15:01:40 2012 +0200

    adding tests to plato-model (model and tree)

[33mcommit 90ca18ed10c7682cb65e20f966b21f15703c4db9[m
Author: Michael Kraxner <kraxner@ifs.tuwien.ac.at>
Date:   Tue Jun 26 13:59:13 2012 +0200

    comments on todo #34

[33mcommit ffef03e8818d50f5bf4850778448407dfc5a1731[m
Author: Markus Plangg <plangg@ifs.tuwien.ac.at>
Date:   Tue Jun 19 11:33:59 2012 +0200

    Updated DominatedSetCalculator, Criteria Set heading, KBrowser headings

[33mcommit 86c94d724a7d384580f6659152dd2438770d9289[m
Author: Markus Plangg <plangg@ifs.tuwien.ac.at>
Date:   Fri Jun 15 19:37:04 2012 +0200

    Continued on DominatedSetCalculator

[33mcommit 85bd8f220ba8fc329afb9df8b4c1e58b4dab0fb1[m
Author: Michael Kraxner <michael.kraxner@gmail.com>
Date:   Fri Jun 15 15:06:35 2012 +0200

    * added comments
    * logging of intermediary results
    * bugfixes
    (#16)

[33mcommit 217cff0eef1d766fef9d6be269ee7cb0974e0ba8[m
Author: Michael Kraxner <kraxner@ifs.tuwien.ac.at>
Date:   Thu Jun 14 22:05:10 2012 +0200

    first version of IF19: robustness: this needs serious testing #16

[33mcommit f2b158117e49f146a3692149d8a2b3cc5f0cd20c[m
Author: Markus Plangg <plangg@ifs.tuwien.ac.at>
Date:   Thu Jun 14 20:37:50 2012 +0200

    Updated VPlanLeaf for min/maxPotention, Started DominatedSetCalculator

[33mcommit 5abb26c1b0887c75243648722f974497f51aa89b[m
Author: Michael Kraxner <kraxner@ifs.tuwien.ac.at>
Date:   Thu Jun 14 20:05:13 2012 +0200

    * started calculating robustness ( #16 )
    * cleaned up unused, ... code ( #21 )

[33mcommit e78d87f26173ce108b13794adac0b58474a9178e[m
Author: Michael Kraxner <kraxner@ifs.tuwien.ac.at>
Date:   Thu Jun 14 17:42:56 2012 +0200

    * extended ImportanceAnalysis: gets PlanInfos
    ( refs #16)

[33mcommit 67c1c43cffe2cac386d3f431e5c6c429c17601b7[m
Author: Michael Kraxner <kraxner@ifs.tuwien.ac.at>
Date:   Thu Jun 14 17:18:37 2012 +0200

    * added PlanInfo, which caches id and overall results for each alternative
    (refs #16 )

[33mcommit d20dfe7dff5b4e417c509c9ecc6a2d8e4fc601c4[m
Author: Michael Kraxner <kraxner@ifs.tuwien.ac.at>
Date:   Thu Jun 14 16:49:28 2012 +0200

    * calculating results for selected plans
    * cleaning up
    ( refs #21 #16)

[33mcommit b7d1d25652bf6a069e9d1f79059409c6e0ce1fe8[m
Author: Michael Kraxner <kraxner@ifs.tuwien.ac.at>
Date:   Wed Jun 13 18:00:12 2012 +0200

    * added csv export for impact factors table
    * renamed criticality to selectivity
    ( #16 )

[33mcommit ed72cfc48e43dca3e611aa20e23930d11eb5bdbe[m
Author: Michael Kraxner <kraxner@ifs.tuwien.ac.at>
Date:   Wed Jun 13 14:21:33 2012 +0200

    error msg for invalid pwd are now shown and logged fixes #15

[33mcommit 114d192d59d5d275cb3255e76d78af4d2c256f6c[m
Author: Michael Kraxner <kraxner@ifs.tuwien.ac.at>
Date:   Wed Jun 13 13:47:47 2012 +0200

    Changed method signatures for sorting in KBrowser
    Apparently, since JBoss 7.1 EL is handling types more strictly/whole numbers are interpreted as long and cannot be converted to ints
    fixes #13

[33mcommit cbfb924e5d9e47d407399e1646aced3e81fbd443[m
Author: Michael Kraxner <kraxner@ifs.tuwien.ac.at>
Date:   Wed Jun 13 13:46:52 2012 +0200

    I