(:*******************************************************:)
(: Test: K-SeqExprCast-1107                              :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:39+02:00                       :)
(: Purpose: 'castable as' involving xs:gMonthDay as source type and xs:QName as target type should always evaluate to false. :)
(:*******************************************************:)
not(xs:gMonthDay("--11-13") castable as xs:QName)