/* ArrayList.c
 * Jonathan Velez
 * 
 * This programming recitation is used to demonstrate the
 * fundamentals of memory allocation and passing references.
 *
 * ACCOMPANYING FILES:
 * ArrayList.h
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "ArrayList.h"

ArrayList *createArrayList( int length ) 
{

    ArrayList *newList = malloc( sizeof(ArrayList) );
    if( newList == NULL) return NULL;

    newList->size = 0;
    newList->capacity = 
		( length > DEFAULT_INIT_LEN )? length : DEFAULT_INIT_LEN;

    newList->array = calloc( newList->capacity, sizeof(char*) );
    
    if( newList->array == NULL ) 
	{
            free( newList );
            return NULL;
    }

    // output the size of the newly created 'ArrayList' and return the list
    printf("-> Created new ArrayList of size %d.\n", newList->capacity);

    return newList;
}   // end createArrayList( int )


ArrayList *destroyArrayList( ArrayList *list ) 
{
    int i;

    if( list == NULL ) return NULL;

    // free each element of the array, the array itself, and the 
  	// ArrayList in which they were encapsulated
    for( i = 0; i < list->size; i++ ) 
	{
        free( list->array[i] );
    }   
	free( list->array );
    free( list );

    return NULL;
}   // end *destroyArrayList( ArrayList )


ArrayList *expandArrayList( ArrayList *list, int length ) 
{
    int i;

    if( list==NULL ) return NULL;
    if( length <= list->capacity ) return list;

    // reallocate memory and expand the array to store 'length' number of
	  // elements
    list->array = realloc( list->array, length*sizeof(char*) );
    if( list->array == NULL ) 
    {
        free( list->array );
        free( list );
        return NULL;
    }

    // initialize the empty elements to NULL and update the maximum capacity of the array
    for( i = length-list->capacity-1; i < length; i++ ) list->array[i] = NULL;
    list->capacity = length;

    // output the new size expansion and return the list
    printf("-> Expanded ArrayList to size %d.\n", list->capacity);
    return list;
}   //end *expandArrayList( ArrayList, int )


ArrayList *trimArrayList( ArrayList *list ) 
{
    // validate parameters
    if( list==NULL ) return NULL;

    // realloc only enough memory for the current size of the array
    // and update its maximum capacity.
    if( list->capacity > list->size ) 
    {
        list->array = realloc( list->array, list->size*sizeof(char*) );
        if( list->array == NULL ) 
        {
            free( list->array );
            free( list );
            return NULL;
        }
        list->capacity = list->size;
        printf("-> Trimmed ArrayList to size %d.\n", list->capacity );
    }
    
    return list;
}   // end *trimArrayList( ArrayList )


char *put( ArrayList *list, char *str ) 
{
    
    if( list==NULL || str==NULL) return NULL;

    // expand the array if full
    if( list->size == list->capacity )
        expandArrayList( list, (list->capacity*2)+1 );

    // realloc enough memory to store the value of 'str' within the first 
    // available element within the array. copy in 'str' and update 'size'
    list->array[list->size] = 
		realloc( list->array[list->size], (strlen(str)+1)*sizeof(char) );
    if( list->array[list->size] == NULL ) return NULL;
    strcpy( list->array[list->size++], str );

    // return the newly placed string
    return list->array[list->size - 1];
}   // end *put( ArrayList, char* )


char *get( ArrayList *list, int index ) 
{
    // validate parameters
    if( list == NULL || index >= list->size || index < 0 ) return NULL;

    // return the string stored at element 'index'
    return list->array[index];
}   // end *get( ArrayList, int )


char *set( ArrayList *list, int index, char *str ) 
{
    // validate parameters
    if( str == NULL || list == NULL || index >= list->size || index < 0 ) return NULL;

    // realloc enough memory to copy in 'str' to the element at 'index'
    list->array[index] = realloc( list->array[index], (strlen(str)+1)*sizeof(char) );
    strcpy( list->array[index], str );

    // return the newly set string stored at element 'index'
    return list->array[index];
}   // end *set( ArrayList, int, char* )


char *insertElement( ArrayList *list, int index, char *str ) 
{
    int i;

    // validate parameters
    if( list == NULL || str == NULL || index < 0 ) return NULL;

    // expand the list if full
    if( list->size == list->capacity )
        expandArrayList( list, (list->capacity*2)+1 );

    // insert the element at the end of the array if the index exceeds its
    // current size
    if( index >= list->size ) 
    {
        list->array[list->size] = realloc( list->array[list->size], (strlen(str)+1)*sizeof(char) );
        strcpy( list->array[list->size], str );

        // update the size of the list and return the newly inserted element
        list->size++;
        return list->array[list->size-1];
    }

    // initialize 'i' to the first available element at the end of the 
    // array, and realloc enough memory to copy in the value stored in the
    // preceding element ('i-1'). continue shifting elements to the right 
    // until reaching the'index' and copy in the value stored in 'str'
    for( i = list->size; i > index; i-- ) 
    {
        list->array[i] = realloc( list->array[i], strlen(list->array[i-1]+1)*sizeof(char) );
        strcpy( list->array[i], list->array[i-1] );
    }   
    list->array[index] = realloc( list->array[index], strlen(str)*sizeof(char) );
    strcpy( list->array[index], str );

    // update the size of the list and return the newly inserted element
    list->size++;
    return list->array[index];
}   // end *insertElement( ArrayList, int, char* )


int removeElement( ArrayList *list, int index ) 
{
    int i;

    // validate parameters
    if( list == NULL || index >= list->size || index < 0 ) return 0;

    // initalize 'i' to 'index' in order to remove the element indicated 
    // by 'index' and shift the elements. for each i'th element, realloc 
    // memory and overwrite its data with the element to its right.
    // remove the last occupied element of the array by reallocating its
    // memory and setting its value NULL
    for( i = index; i < list->size-1; i++) 
    {
        list->array[i] = realloc( list->array[i], (strlen(list->array[i+1])+1)*sizeof(char) );
        strcpy( list->array[i], list->array[i+1] );
    }   
    
    free( list->array[--list->size] );
    list->array[list->size] = NULL;

    // return TRUE once the element is removed
    return 1;
}   // end removeElement( ArrayList, int )


int getSize( ArrayList *list ) 
{
    // validate parameters
    if( list == NULL ) return -1;

    else return list->size;
}   // end getSize( ArrayList )


void printArrayList( ArrayList *list ) 
{
    int i;

    // validate parameters
    if ( list==NULL || list->size==0 ) printf("(empty list)\n" );

    // output each stored element within the array
    else for( i=0; i < list->size; i++ ) printf("%s\n", list->array[i]);
}   // end printArrayList( ArrayList )
