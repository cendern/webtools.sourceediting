(:*******************************************************:)
(: Test: K-GenCompNE-10                                  :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: General comparison causing numeric promotion from xs:untypedAtomic. :)
(:*******************************************************:)
not("2" = xs:untypedAtomic("1"))