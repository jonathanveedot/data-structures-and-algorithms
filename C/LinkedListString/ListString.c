/**
* ListString.c
* Jonathan Velez
* 
* This programming recitation demonstrates some of the fundamentals of
* the Linked List data structure. This program uses linked lists to
* represent strings and implements functions to manipulate them.
*
* Each input file will begin with a single string that contains at least 1
* character and no more than 1023 characters, and no spaces. That string,
* the working string, is to be stored in a LinkedList in which each node
* contains a single character. The working string will be manipulated 
* according to the remaining commands in the input file. 
* 
* Each of the remaining lines in the file will correspond to one of the 
* following string manipulation commands:
* - "@ key str" : key is a single character. Replace all instances of key
*   with str in the working string.
* - "- key" : key is a single character. Delete all instances of key (if 
*   any) from the working string. 
* - "~" : Reverse the working string.
* - "!" : Print the working string.
* 
* Input files are passed in at the command line. 
*
* ACCOMPANYING FILES:
* ListString.h
*/

#include <stdio.h>
#include <stdlib.h>
#include "ListString.h"

// creates a new node
node *createNode( char data ) 
{
    node *n = malloc( sizeof(node) );
    if(n == NULL) 
    {
        printf("ERROR: out of memory in *createNode( char data )\n");
        return NULL;
    }
    n->data = data;
    n->next = NULL;

    return n;
}


// inserts a node at the front of a LinkedList
void *insertNode( node *head, char data ) 
{
    node *n;

    if(head == NULL)
        return createNode( data );

    n = createNode( data );
    n->next = head;
    return n;
}


// stores a String as a Linked List
node *stringToList( char *str ) 
{
    node *head = NULL;
    int length, i;

    if(str == NULL || str == "")
        return NULL;

    // count the length of the string
    length = 0;
    while(*(str+length) != '\0')
        length++;

    // strip each letter of the string in reverse order to be able to
    // insert each new node at the front of the LinkedList
    for(i = 0; i<length; i++)
        head = insertNode( head, str[length-i-1] );

    return head;
}


// this recursively searches out/deletes the key from a Linked List
node *rec_del( node *head, char key ) 
{
    node *temp;

    if(head == NULL)
        return NULL;

    if(head->data != key) 
    {
        head->next = rec_del( head->next, key );
        return head;
    }

    temp = head->next;
    free( head );
    return rec_del( temp, key );
}


// this recursively searches out/replaces the key from a LinkedList
node *rec_replace( node *head, char key, char *str ) 
{
    node *temp, *trash, *n;

    if(head == NULL)
        return NULL;

    // if the current node's data does not match the key,
    // continue searching through the LinkedList
    if(head->data != key) {
        head->next = rec_replace( head->next, key, str );
        return head;
    }

    // trash the current node, replace it with the intended String.
    trash = head;
    n = head->next;
    free( trash );
    head = stringToList( str );
    // go to the last node of the replacement String and attach the
    // rest of the LinkedList. continue searching out they key.
    for(temp = head; temp->next != NULL; temp = temp->next)
        ;
    temp->next =  rec_replace( n, key, str );

    return head;
}


// searches through a LinkedList for a character key and replaces/deletes it
node *replaceChar( node *head, char key, char *str ) 
{
    if(head == NULL)
        return NULL;

    if(str == NULL || *str == '\0')
        return rec_del( head, key );

    return rec_replace( head, key, str );
}


// reverses the contents of a LinkedList
node *reverseList( node *head ) 
{
    node *temp, *reverse = NULL;

    if(head == NULL)
        return NULL;

    // 'reverse' contains (points to) the empty reverse string.
    // first pass the head into 'reverse', and set head->next to NULL.
    // shift the remaining elements to the front of 'reverse' node.
    // the last node of 'head' will now be the head of 'reverse', and what
    // was originally the head will now be at the tail of the LinkedList.
    while(head != NULL) 
    {
        temp = head->next;
        head->next = reverse;
        reverse = head;
        head = temp;
    }

    return reverse;
}


// prints the contents of a Linked List
void printList( node *head ) 
{
    if(head == NULL) 
    {
        printf("(empty string)\n");
        return;
    }

    printf("%c%s", head->data, head->next != NULL? "" : "\n" );

    if(head->next != NULL)
        printList( head->next );

}


int main( int argc, char **argv ) 
{
    FILE *fin;

    char str[1024], command, key;
    node *word;
    int i;

    // iterate through multiple input files at the command line
    for(i = 1; i < argc; i++) 
    {
        //open the input file
        fin = fopen( argv[i], "r" );
        if(fin == NULL) 
        {
            fprintf(stderr, "ERROR: Unable to open %s\n", argv[i]);
            exit(1);
        }

        // scan in the String to be manipulated and store as a LinkedList
        fscanf(fin, "%s", str);
        word = stringToList( str );

        // scan in commands until EOF
        while(fscanf(fin, "%c", &command) != EOF) 
        {
            switch( command ) 
            {
                case( '@' ): 
                {
                    fscanf( fin, " %c %s", &key, str );
                    word = replaceChar( word, key, str );
                    break;
                }
                case( '-' ): 
                {
                    fscanf( fin, " %c", &key );
                    word = replaceChar( word, key, NULL );
                    break;
                }
                case( '~' ): 
                {
                    word = reverseList( word );
                    break;
                }
                case( '!' ): 
                {
                    printList( word );
                    break;
                }

            }   // end switch

        }   // end while

    }   // end for

    return 0;
}
