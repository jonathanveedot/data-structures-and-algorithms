/*
 * BaseConversions.c
 * =======
 * This programming recitation reviews the fundamentals of base conversions.
 * There are various numbering systems (such as binary, decimal, and hex),
 * and sometimes we need to convert between bases before performing 
 * computation. These are functions for converting between bases. At the 
 * command line, be sure to compile with the -lm flag in order to link 
 * the math library, like so:
 *    gcc bconv.c -lm
 */

#include <stdio.h>
#include <string.h>
#include <math.h>

// This global array can be accessed when converting to bases between 11 and 16.
char base16[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                 '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

// Converts n from base 10 to the base specified. This recursive function is
// called by bconv(). Two issues: This doesn't print "0" if n is zero in the
// initial call, and this doesn't support printing a newline character when it's
// finished. That's why bconv() is used as a wrapper to this function.
void bconv_helper(int n, int base)
{
	if (n == 0)
		return;

	bconv_helper(n / base, base);
	printf("%c", base16[n % base]);
}

// Convert n from base 10 to some other base. Relies on the bconv_helper()
// function, unless n is zero.
void bconv(int n, int base)
{
	if (n == 0)
		printf("0");
	else
		bconv_helper(n, base);

	printf("\n");
}

// Convert a string like "1101" to base 10 (from the specified base), using
// Horner's rule. This only works for 2 <= base <= 10.
int toBase10(char *s, int base)
{
	int i, len = strlen(s), ret = 0;

	for (i = 0; i < len; i++)
	{
		ret *= base;
		ret +=  (s[i] - '0');
	}

	return ret;
}

// This is another approach for converting to base 10, but it uses
// inefficient repeated calls to the pow() function.
int toBase10_old(char *s, int base)
{
	int i, len = strlen(s), ret = 0;

	for (i = 0; i < len; i++)
	{
		ret +=  (s[i] - '0') * pow(base, len - i - 1);
	}

	return ret;
}

// Example function calls.
int main(void)
{
	bconv(16, 16);  // 10
	bconv(10, 16);  // a

	printf("%d\n", toBase10("1111", 2));
	printf("%d\n", toBase10_old("1111", 2));

	printf("%d\n", toBase10("1010", 2));
	printf("%d\n", toBase10_old("1010", 2));

	return 0;
}
