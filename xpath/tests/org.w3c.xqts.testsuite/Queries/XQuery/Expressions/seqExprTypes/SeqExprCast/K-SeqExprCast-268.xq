(:*******************************************************:)
(: Test: K-SeqExprCast-268                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: Testing timezone field in xs:gMonth: the minute component cannot be -60. :)
(:*******************************************************:)
xs:gMonth("--08-10:60")