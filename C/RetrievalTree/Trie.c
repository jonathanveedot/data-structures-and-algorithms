/**
 * Trie.c
 * Jonathan Velez
 *
 * This is a programming recitation that demonstrates some of the
 * fundementals of Retrieval Trees, a data structure used to represent
 * sequences of symbols according to the trees' edges. This advanced tree
 * structure allows for efficient look-up and insertion. Worst-case run-
 * time for each is O(k), where k is the length of the sequence of
 * symbols.
 *
 * This program takes two command-line arguments, specifying two files to
 * be read at runtime:
 *
 * ./a.out corpus01.txt input01.txt
 *
 * The first filename specifies a corpus that will be used to construct
 * trie and subtries. The corpus file will contain a series of sentences,
 * each line containing a single sentence with at least 1 word and no more
 * than 20 words. Each word contains fewer than 1024 characters. Each
 * sentence is terminated with a single period, and that period is always
 * preceded by a space. Each word in the corpus should be inserted into
 * the main trie. A count variable should track the frequency of occurence
 * of each word. There should be a subtrie for each word in the corpus to
 * track the count of co-occurrences of each pair of words. If a string in
 * main trie does not co-occur with any other words, its subtrie pointer
 * should be NULL.
 *
 * The second filename specifies an input file with strings to be
 * processed based on the contents of the trie. This file will contain any
 * number of lines of text. Each line will contain either one word or an
 * exclamation point, "!". If an exclamation point is encountered, the
 * program should print strings represented in the main tree in
 * alphabetical order with the occurrence count in parentheses. Otherwise,
 * if a string is encountered, it will consist strictly of alphabetical
 * symbols, upper- or lower-case. The string will contain fewer than 1024
 * characters. These strings are processed as follows:
 * - if the string encountered is in the tree, the program prints the
 *   string, followed by a printout of its subtrie contents.
 * - if the string encountered is in the trie, but its subtrie is
 *   empty, the program prints the string, followed by "(EMPTY)"
 *   on the next line.
 * - if the string encountered is not in the trie at all, the program
 *   should print the string, followed by "(INVALID STRING)" on the next
 *   line.
 *
 * Print the output to a file, "printTrie.txt"
 *
 * ACCOMPANYING FILES:
 * Trie.h
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Trie.h"

#define WORD_MAX_LEN 1024   // 1023 + 1 for '\0'
#define SENTENCE_MAX_LEN 21 // 20 + 1 for '.'

TrieNode *createTrieNode( void ) 
{
    TrieNode *trie = calloc( 1, sizeof(TrieNode) );
    if(trie == NULL) 
    {
        fprintf(stderr, "\nERROR in createTrieNode(): Out of memory.\n");
        return NULL;
    }
}

// inserts a word into the trie
void insertString( TrieNode *root, char *str ) 
{
    int letter;

    if(root == NULL || str == NULL || *str == '.') 
    {
        return;
    }

    if(*str == '\0') 
    {
        root->count++;
        return;
    }

    letter = (*str - 'a' < 0) ? *str - 'A' : *str - 'a';

    if(root->children[letter] == NULL) 
    {
        root->children[letter] = createTrieNode();
        insertString( root->children[letter], str+1 );
        return;
    }

    insertString( root->children[letter], str+1 );
}

// searches for a word within the trie and returns its terminating root
// if it is in the trie
TrieNode *getNode( TrieNode *root, char *str ) 
{
    int letter;

    if(*str == '\0' && root->count == 0)
        return NULL;

    if(*str == '\0' && root->count != 0)
        return root;

    letter = (*str - 'a' < 0) ? *str - 'A' : *str - 'a';
    if(root->children[letter] != NULL)
        return getNode( root->children[letter], (str+1) );

    return NULL;
}

// this is a helper function embedded within buildSubTries():
// processes each individual sentence and updates subtries
void processSentence( TrieNode *root, char* *sentence, int numWords ) 
{
    TrieNode *lex;
    int newInstance = 1;
    int i, j;

    for(i=0; i<numWords; i++) 
    {
        // check to see if the current word has already been processed
        for(j = 0; j < i; j++) 
        {
            if(!strcasecmp( *(sentence+i), *(sentence+j) ))
                newInstance = 0;
        }

        // retrieve the current word from the Trie
        lex = getNode( root, *(sentence+i) );

        // if this word has not previously occured build its subtrie
        if(newInstance) 
        {
            for(j = 0; j < numWords; j++) 
            {
                // skip over co-occurences of the same word
                if(!strcasecmp( *(sentence+i), *(sentence+j) ))
                    continue;
                // root the subtrie if empty
                if(lex->subtrie == NULL)
                    lex->subtrie = createTrieNode();
                // insert the co-occurence into the current subtrie
                insertString( lex->subtrie, *(sentence+j) );
            }
        }
        // reset flag
        newInstance = 1;
    }
}

void buildSubTries( char *filename, TrieNode *root ) 
{
    FILE *fin;
    char* *sentence;
    int i, count = 0;

    // allocate memory to temp store each sentence for further processing
    sentence = malloc( SENTENCE_MAX_LEN * sizeof(char*) );
    for(i = 0; i < SENTENCE_MAX_LEN; i++)
        sentence[i] = malloc( WORD_MAX_LEN * sizeof(char) );

    fin = fopen( filename, "r" );
    if(fin == NULL) 
    {
        fprintf(stderr,"ERROR: Unable to open %s in buildSubTries\n",filename);
        return;
    }

    // scan in each sentence, word by word, for temp storage
    while(fscanf( fin, "%s", (*(sentence+count)) ) != EOF) 
    {
        // find the end of each sentence and process its contents
        if( **(sentence+count) == '.' ) 
        {
            processSentence( root, &(*sentence), count );
            count = 0;
            continue;
        }
        count++;
    }

    // clean-up
    for(i = 0; i < SENTENCE_MAX_LEN; i++)
        free( sentence[i] );
    free( sentence );
    fclose( fin );
}

// builds the initial trie; counting all the word frequencies
// then calls on a helper function, buildSubTries(), to count
// all the co-occurences of words
TrieNode *buildTrie( char *filename ) 
{
    FILE *fin;
    TrieNode *root;
    char *buffer = malloc( WORD_MAX_LEN * sizeof(char) );

    if(filename == NULL)
        return NULL;

    fin = fopen( filename, "r" );
    if(fin == NULL) 
    {
        fprintf(stderr, "ERROR: Unable to open %s in buildTrie()\n", filename);
        return NULL;
    }

    // root the Trie
    root = createTrieNode();
    // build the Trie
    while(fscanf( fin, "%s ", buffer) != EOF)
        insertString( root, buffer );
    fclose( fin );

    buildSubTries( filename, root );

    free( buffer );

    return root;
}

printTrieHelper(TrieNode *root, char *buffer, int k) 
{
    int i;

    if (root == NULL)
        return;

    if (root->count > 0)
        printf("%s (%d)\n", buffer, root->count);

    buffer[k + 1] = '\0';
    for (i = 0; i < 26; i++) 
    {
        buffer[k] = 'a' + i;
        printTrieHelper(root->children[i], buffer, k + 1);
    }
    buffer[k] = '\0';
}

// If printing a subtrie, the second parameter should be 1; otherwise, 0.
void printTrie(TrieNode *root, int useSubtrieFormatting) 
{
    char buffer[WORD_MAX_LEN+2];

    if (useSubtrieFormatting) 
    {
        strcpy(buffer, "- ");
        printTrieHelper(root, buffer, 2);
    }
    else 
    {
        strcpy(buffer, "");
        printTrieHelper(root, buffer, 0);
    }
}


int main( int argc, char **argv ) 
{
    FILE *fin;
    TrieNode *r, *s;
    char *buffer = malloc( WORD_MAX_LEN * sizeof(char) );

    // process the corpus
    if(argc < 2) 
    {
        fprintf(stderr, "ERROR in main(): no corpus file specified\n");
        exit(1);
    }
    r = buildTrie( argv[1] );

    // process the input file
    if(argc < 3) 
    {
        fprintf(stderr, "ERROR in main(): no input file specified\n");
        exit(1);
    }
    fin = fopen( argv[2], "r" );
    if (fin == NULL)
        fprintf(stderr, "ERROR: unable to open %s in main()\n", argv[2]);

    // scan the input file's commands
    while(fscanf(fin, "%s ", buffer) != EOF) 
    {
        // print the list if a '!' is scanned in
        if( !strcmp(buffer, "!") ) 
        {
            printTrie( r, 0 );
            continue;
        }

        // otherwise search the Trie for each string and print its subtrie
        s = getNode( r, buffer );
        printf("%s\n", buffer);

        if(s == NULL)
            printf("(INVALID STRING)\n");
        else if(s->subtrie == NULL)
            printf("(EMPTY)\n");
        else
            printTrie( s->subtrie, 1 );
    }

    return 0;
}
