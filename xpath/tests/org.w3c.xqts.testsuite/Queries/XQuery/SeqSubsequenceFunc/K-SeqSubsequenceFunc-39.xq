(:*******************************************************:)
(: Test: K-SeqSubsequenceFunc-39                         :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:40+02:00                       :)
(: Purpose: A test whose essence is: `count(subsequence((current-time(), 2 , 3, 4), 2, 2)) eq 2`. :)
(:*******************************************************:)
count(subsequence((current-time(), 2 , 3,  4), 2, 2)) eq 2