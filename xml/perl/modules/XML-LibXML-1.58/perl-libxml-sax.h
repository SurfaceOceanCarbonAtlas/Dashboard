/**
 * perl-libxml-sax.h
 * $Id: perl-libxml-sax.h,v 1.1.1.1 2005/02/07 18:34:08 callahan Exp $
 */

#ifndef __PERL_LIBXML_SAX_H__
#define __PERL_LIBXML_SAX_H__

#ifdef __cplusplus
extern "C" {
#endif

#include <libxml/tree.h>

#ifdef __cplusplus
}
#endif

/* has to be called in BOOT sequence */
void
PmmSAXInitialize(pTHX);

void
PmmSAXInitContext( xmlParserCtxtPtr ctxt, SV * parser );

void 
PmmSAXCloseContext( xmlParserCtxtPtr ctxt );

xmlSAXHandlerPtr
PSaxGetHandler();

#endif
