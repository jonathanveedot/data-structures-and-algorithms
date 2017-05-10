/* Fibonacci.c
 * Jonathan Velez
 * 
 * This programming recitation demonstrates some of the fundamental
 * concepts of iteration versus recurssion, top-down versus bottom-up 
 * processing, and huge integers. This program implements an iterative
 * bottom-up solution with linear runtime, in contrast to the top-down 
 * recurssive function with exponential runtime. Fib(n) computes the
 * n'th term of the Fibonacci sequence while overcoming the limitations
 * of C's 32 bit integers by storing very large integers in arrays of 
 * individual digits. 
 * 
 * ACCOMPANYING FILES:
 * Fibonacci.h
 */

#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include "Fibonacci.h"

/* HugeInteger *parseInt( unsigned int )
 * This function stores an unsigned int 'n' within a HugeInteger Struct by parsing
 * its digits and storing these elements backwards within the within the array.
 * Returns: a pointer to the newly allocated HugeInteger Struct or NULL if allocation
 * fails at any point.
 */
HugeInteger *parseInt( unsigned int n ) 
{
    int i;
    unsigned int count = n;

    HugeInteger *p = calloc( 1, sizeof(HugeInteger) );
    if( p == NULL )   return NULL;

    // count the number of digits in int 'n'
    while( count != 0 ) 
    {
        count/=10;
        p->length++;
    }
    if(n == 0) p->length++;

    // dynamically allocate memory for the integer array according to length
    p->digits = calloc( p->length, sizeof(int*) );
    if( p->digits == NULL ) 
    {
        free( p );
        return NULL;
    }
    // store int 'n' within the p->digits array by populating the
    // array backwards, digit by digit
    for( i = 0; i < p->length; i++ ) 
    {
        p->digits[i] = n%10;
        n/=10;
    }

    return p;
}   // end HugeInteger *parseInt( unsigned int )




/* HugeInteger *parseString( char* )
 * Converts a number from string format to HugeInteger format.
 * If an empty string ("") is passed to this function, treat it
 * as a zero ("0")
 * Returns: a pointer to the newly allocated HugeInteger struct,
 * or NULL if dynamic memory allocation fails or if str is NULL.
 */
HugeInteger *parseString( char *str ) 
{
    int i;
    HugeInteger *p;

    if(str == NULL) return NULL;

    p = calloc( 1, sizeof(HugeInteger) );
    if(p == NULL) return NULL;

    // if 'str' = "", store the value '0'
    if(*str == '\0') 
    {
        p->length = 1;
        p->digits = calloc( p->length, sizeof(int*) );
        if(p->digits == NULL) 
        {
            free( p );
            return NULL;
        }
        *p->digits = 0;

        return p;
    }
    else 
    {
        // count the number of digits in the string
        while(*(str+p->length) != '\0') p->length++;
        // dynamically allocate an array of size 'length' to hold the digits
        p->digits = calloc( p->length, sizeof(int*) );
        if(p->digits == NULL) 
        {
            free( p );
            return NULL;
        }
        // populate the array backwards, digit by digit
        for(i = 0; i < p->length; i++)
            *(p->digits+i) = *(str+p->length-1-i)-'0';

        return p;
    }
}   // end HugeInteger *parseString( char* )




/* HugeInteger *hugeDestroyer( HugeInteger )
 * Destroy any and all dynamically allocated memory associated with p.
 * Returns: NULL
 */
HugeInteger *hugeDestroyer( HugeInteger *p ) 
{
    if(p == NULL)
        return NULL;

    if(p->digits == NULL) 
    {
        free( p );
        return NULL;
    }
    free( p->digits );
    free( p );
    p = NULL;
    return p;
}   // end HugeInteger *hugeDestroyer( HugeInteger )




/* unsigned int *toUnsignedInt( HugeInteger )
 * Convert the integer represented by p to a dynamically allocated
 * unsigned int. If the value of 'p' is greater than the value of
 * 'UINT_MAX' then it cannot be assigned as an 'unsigned int'.
 * Returns: A pointer to the dynamically allocated unsigned integer
 * or NULL if the value cannot be represented as an integer (or if
 * a NULL value is passed into the function)
 */
unsigned int *toUnsignedInt( HugeInteger *p ) 
{
    int i, magnitude;
    unsigned int *n;
    HugeInteger *hugeUINT_MAX;

    if(p == NULL) return NULL;

    // store the value of 'UINT_MAX' as a 'HugeInteger'
    hugeUINT_MAX =  parseInt( UINT_MAX );
    if(hugeUINT_MAX == NULL) return NULL;

    // if 'p' is not less than 'hugeUINT_MAX' then it cannot be
    // stored as an unsigned int and the function should return NULL;
    if(p->length > hugeUINT_MAX->length) 
    {
            hugeDestroyer( hugeUINT_MAX );
            return NULL;
    }
    // compare digit by digit to ensure p < hugeUINT_MAX
    if(p->length == hugeUINT_MAX->length) 
    {
        for(i = hugeUINT_MAX->length-1; i >= 0; i--) 
        {
            if(p->digits[i] > hugeUINT_MAX->digits[i]) 
            {
                hugeDestroyer( hugeUINT_MAX );
                return NULL;
            }
            else if(p->digits[i] < hugeUINT_MAX->digits[i]) 
            {
                break;
            }
        } // end for
    }
    // store 'p' as an unsigned int by multiplying each digit
    // by its magnitude and adding it into 'n'
    n = malloc( sizeof(unsigned int) );
    if(n == NULL) 
    {
        hugeDestroyer( hugeUINT_MAX );
        return NULL;
    }
    *n = 0, magnitude = 1;
    for(i = 0; i < p->length; i++) 
    {
        *n += p->digits[i] * magnitude;
        magnitude *= 10;
    }
    hugeDestroyer( hugeUINT_MAX );

    return n;
}   // end unsigned int *toUnsignedInt( HugeInteger )




/* HugeInteger *hugeAdd( HugeInteger, HugeInteger )
 * Dynamically allocates a HugeInteger struct that contains the
 * result of adding the huge integers represented by 'p' and 'q'.
 * Returns: A pointer to the newly allocated HugeInteger struct
 * or NULL if a NULL pointer is passed to this function or if any
 * memory allocation fails within the function.
 */
HugeInteger *hugeAdd( HugeInteger *p, HugeInteger *q ) 
{
    int i;
    HugeInteger *r, *temp;

    if(p == NULL || q == NULL) return NULL;
    // ensures that 'p' will hold the larger value to avoid a
    // segfault when adding the values iteratively down below
    if(p->length < q->length) 
    {
        temp = p;
        p = q;
        q = temp;
    }

    r = malloc( sizeof(HugeInteger) );
    if(r == NULL) return NULL;

    // allocate memory to 'r->digits' but add some extra space
    // in case carrying over a digit can possibly increase the MSD
    r->digits = calloc( p->length+1, sizeof(int*) );
    if( r->digits == NULL ) 
    {
        free( r );
        return NULL;
    }

    // add 'p' and 'q' into 'r'. If the length of 'p' and 'q' differ
    // then q->digits[i] may not contain a valid memory address, and only
    // p->digits[i] should be added into r->digits[i]
    for( i = 0; i < p->length; i++ ) 
    {
        r->digits[i] += (i < q->length) ?
            p->digits[i] + q->digits[i] : p->digits[i];
        // dont forget to carry over the '1'!
        if( r->digits[i]>9 ) 
        {
            r->digits[i]%=10;
            r->digits[i+1]++;
        }
    }

    // set 'r->length' with respects to 'p->length' and add '1' if adding
    // the two values results in an increase in its MSD (i.e. the extra
    // allocated space within 'digits' does not contain a zero ('0') value)
    r->length = p->length;
    if( p->length < q->length+3 && r->digits[r->length] != 0) r->length++;

    return r;
}   // end HugeInteger *hugeAdd( HugeInteger, HugeInteger )




/* HugeInteger *fib( int )
 * This is a Fibonacci function designed to calculate Fibonacci
 * numbers as a 'HugeInteger' in order to overcome the limitations
 * of the 'int' data size.
 * Returns: a pointer to a HugeInteger representing F(n) or NULL
 * if dynamic memory allocation fails.
 */
HugeInteger *fib( int n ) 
{
    int i;
    HugeInteger *p, *q, *r;

    // F_(0) = 0
    p = parseInt(0);
    if(p == NULL)
        return NULL;
    // F_(1) = 1
    q = parseInt(1);
    if(q == NULL) 
    {
        hugeDestroyer( p );
        return NULL;
    }

    // Calculate F_(n)
    if(n == 0) 
    {
        hugeDestroyer( q );
        return p;
    }
    else if(n == 1) 
    {
        hugeDestroyer( p );
        return q;
    }
    // F_(n>1) = F_(n-1) + F_(n-2)
    else 
    {
        for(i = 2; i <= n; i++) 
        {
            r = hugeAdd( p, q );
            if(r == NULL) 
            {
                hugeDestroyer( p );
                hugeDestroyer( q );
                return NULL;
            }
            // F_(n-1) is no longer needed
            // F_(n-2) becomes F_(n-1)
            // and F_(n-1) becomes F_(n)
            hugeDestroyer( p );
            p = q;
            q = r;
        }
        hugeDestroyer( p );
        return r;
    }
} // end HugeInteger *fib( int )
