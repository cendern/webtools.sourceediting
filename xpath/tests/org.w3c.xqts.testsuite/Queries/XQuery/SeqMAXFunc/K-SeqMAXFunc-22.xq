(:*******************************************************:)
(: Test: K-SeqMAXFunc-22                                 :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:41+02:00                       :)
(: Purpose: An xs:string cannot be compared to a numeric, even if a value is NaN. :)
(:*******************************************************:)
max(("a string", 1, xs:float("NaN")))