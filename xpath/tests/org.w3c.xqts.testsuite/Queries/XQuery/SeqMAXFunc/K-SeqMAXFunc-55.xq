(:*******************************************************:)
(: Test: K-SeqMAXFunc-55                                 :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:41+02:00                       :)
(: Purpose: Testing max() with current-date().           :)
(:*******************************************************:)
max((current-date(), xs:date("1999-01-01"))) eq current-date()