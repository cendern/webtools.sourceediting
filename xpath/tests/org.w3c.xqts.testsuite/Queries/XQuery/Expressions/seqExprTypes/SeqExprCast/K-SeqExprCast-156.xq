(:*******************************************************:)
(: Test: K-SeqExprCast-156                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: Ensure that a negative xs:duration is properly serialized when cast to xs:string. :)
(:*******************************************************:)
xs:string(xs:duration("-P0010DT0010H0010M0010S"))
		eq "-P10DT10H10M10S"