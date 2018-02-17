(:*******************************************************:)
(: Test: K-DayTimeDurationAdd-3                          :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:36+02:00                       :)
(: Purpose: Simple test of adding two arbitrary xs:yearMonthDurations. :)
(:*******************************************************:)
xs:dayTimeDuration("P3DT4H3M3.100S") + xs:dayTimeDuration("P3DT12H31M56.303S")
	                eq xs:dayTimeDuration("P6DT16H34M59.403S")