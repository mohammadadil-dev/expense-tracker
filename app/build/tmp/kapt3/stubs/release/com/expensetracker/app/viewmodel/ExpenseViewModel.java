package com.expensetracker.app.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00de\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b$\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b3\u0018\u00002\u00020\u0001:\u0002\u00e2\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004JG\u0010\u0086\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u0088\u0001\u001a\u00020v2\b\u0010\u0089\u0001\u001a\u00030\u008a\u00012\u0007\u0010\u008b\u0001\u001a\u00020\n2\u0007\u0010\u008c\u0001\u001a\u00020\u000f2\u0007\u0010\u008d\u0001\u001a\u00020\n2\u000f\u0010\u008e\u0001\u001a\n\u0012\u0005\u0012\u00030\u0087\u00010\u008f\u0001J+\u0010\u0090\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u0091\u0001\u001a\u00020\n2\u0007\u0010\u0092\u0001\u001a\u00020\n2\u000f\u0010\u008e\u0001\u001a\n\u0012\u0005\u0012\u00030\u0087\u00010\u008f\u0001JH\u0010\u0093\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u008c\u0001\u001a\u00020\u000f2\u0007\u0010\u0094\u0001\u001a\u00020\n2\u0007\u0010\u0095\u0001\u001a\u00020\n2\u0007\u0010\u008d\u0001\u001a\u00020\n2\t\b\u0002\u0010\u0096\u0001\u001a\u00020\u00112\u000f\u0010\u008e\u0001\u001a\n\u0012\u0005\u0012\u00030\u0087\u00010\u008f\u0001J+\u0010\u0097\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u0098\u0001\u001a\u00020\n2\u0007\u0010\u0099\u0001\u001a\u00020\u000f2\u000f\u0010\u008e\u0001\u001a\n\u0012\u0005\u0012\u00030\u0087\u00010\u008f\u0001J)\u0010\u009a\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u009b\u0001\u001a\u0002012\u0016\u0010\u009c\u0001\u001a\u0011\u0012\u0005\u0012\u00030\u009e\u0001\u0012\u0005\u0012\u00030\u0087\u00010\u009d\u0001J\"\u0010\u009f\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u00a0\u0001\u001a\u00020D2\u000f\u0010\u008e\u0001\u001a\n\u0012\u0005\u0012\u00030\u0087\u00010\u008f\u0001J\u0011\u0010\u00a1\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u00a2\u0001\u001a\u00020AJ\"\u0010\u00a3\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u00a4\u0001\u001a\u00020\u00182\u000f\u0010\u008e\u0001\u001a\n\u0012\u0005\u0012\u00030\u0087\u00010\u008f\u0001J\u0011\u0010\u00a5\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u00a6\u0001\u001a\u00020iJ\u0011\u0010\u00a7\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u0088\u0001\u001a\u00020vJA\u0010\u00a8\u0001\u001a\u00030\u0087\u00012\b\u0010\u00a9\u0001\u001a\u00030\u00aa\u00012\u0016\u0010\u00ab\u0001\u001a\u0011\u0012\u0005\u0012\u00030\u00ac\u0001\u0012\u0005\u0012\u00030\u0087\u00010\u009d\u00012\u0015\u0010\u00ad\u0001\u001a\u0010\u0012\u0004\u0012\u00020\n\u0012\u0005\u0012\u00030\u0087\u00010\u009d\u0001J7\u0010\u00ae\u0001\u001a\u00030\u0087\u00012\u0016\u0010\u00af\u0001\u001a\u0011\u0012\u0005\u0012\u00030\u00b0\u0001\u0012\u0005\u0012\u00030\u0087\u00010\u009d\u00012\u0015\u0010\u00ad\u0001\u001a\u0010\u0012\u0004\u0012\u00020\n\u0012\u0005\u0012\u00030\u0087\u00010\u009d\u0001J\u0011\u0010\u00b1\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u00b2\u0001\u001a\u00020\nJA\u0010\u00b3\u0001\u001a\u00030\u0087\u00012\b\u0010\u00b4\u0001\u001a\u00030\u00b0\u00012\u0016\u0010\u00ab\u0001\u001a\u0011\u0012\u0005\u0012\u00030\u00ac\u0001\u0012\u0005\u0012\u00030\u0087\u00010\u009d\u00012\u0015\u0010\u00ad\u0001\u001a\u0010\u0012\u0004\u0012\u00020\n\u0012\u0005\u0012\u00030\u0087\u00010\u009d\u0001J)\u0010\u00b5\u0001\u001a\u00030\u0087\u00012\b\u0010\u0089\u0001\u001a\u00030\u008a\u00012\u0015\u0010\u009c\u0001\u001a\u0010\u0012\u0004\u0012\u00020\u0011\u0012\u0005\u0012\u00030\u0087\u00010\u009d\u0001J\b\u0010\u00b6\u0001\u001a\u00030\u0087\u0001J\b\u0010\u00b7\u0001\u001a\u00030\u0087\u0001J\b\u0010\u00b8\u0001\u001a\u00030\u0087\u0001J\u0012\u0010\u00b9\u0001\u001a\u00030\u0087\u00012\b\u0010\u00ba\u0001\u001a\u00030\u008a\u0001J\u001a\u0010\u00bb\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u009b\u0001\u001a\u0002012\u0007\u0010\u0092\u0001\u001a\u00020\nJ?\u0010\u00bc\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u00a0\u0001\u001a\u00020D2\u0007\u0010\u008c\u0001\u001a\u00020\u000f2\u0007\u0010\u008d\u0001\u001a\u00020\n2\t\u0010\u0095\u0001\u001a\u0004\u0018\u00010\n2\u000f\u0010\u008e\u0001\u001a\n\u0012\u0005\u0012\u00030\u0087\u00010\u008f\u0001J\n\u0010\u00bd\u0001\u001a\u00030\u0087\u0001H\u0002J\u001a\u0010\u00be\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u009b\u0001\u001a\u0002012\u0007\u0010\u00bf\u0001\u001a\u00020\nJ\u0019\u0010\u00c0\u0001\u001a\u00030\u0087\u00012\u000f\u0010\u008e\u0001\u001a\n\u0012\u0005\u0012\u00030\u0087\u00010\u008f\u0001J\b\u0010\u00c1\u0001\u001a\u00030\u0087\u0001Jw\u0010\u00c2\u0001\u001a\u00030\u0087\u00012\n\u0010\u00c3\u0001\u001a\u0005\u0018\u00010\u008a\u00012\u0007\u0010\u0091\u0001\u001a\u00020\n2\u0007\u0010\u00c4\u0001\u001a\u00020\n2\u0007\u0010\u00c5\u0001\u001a\u00020\u000f2\u0007\u0010\u00c6\u0001\u001a\u00020\u000f2\u0007\u0010\u00c7\u0001\u001a\u00020\u000f2\u0007\u0010\u00c8\u0001\u001a\u00020\n2\t\u0010\u00c9\u0001\u001a\u0004\u0018\u00010\n2\t\u0010\u00ca\u0001\u001a\u0004\u0018\u00010\n2\u000f\u0010\u008e\u0001\u001a\n\u0012\u0005\u0012\u00030\u0087\u00010\u008f\u0001\u00a2\u0006\u0003\u0010\u00cb\u0001J[\u0010\u00cc\u0001\u001a\u00030\u0087\u00012\n\u0010\u00c3\u0001\u001a\u0005\u0018\u00010\u008a\u00012\b\u0010\u0089\u0001\u001a\u00030\u008a\u00012\u0007\u0010\u008b\u0001\u001a\u00020\n2\u0007\u0010\u008c\u0001\u001a\u00020\u000f2\u0007\u0010\u008d\u0001\u001a\u00020\n2\t\b\u0002\u0010\u0096\u0001\u001a\u00020\u00112\u000f\u0010\u008e\u0001\u001a\n\u0012\u0005\u0012\u00030\u0087\u00010\u008f\u0001\u00a2\u0006\u0003\u0010\u00cd\u0001J\u001a\u0010\u00ce\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u0091\u0001\u001a\u00020\n2\u0007\u0010\u00cf\u0001\u001a\u00020\nJ\u0011\u0010\u00d0\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u00d1\u0001\u001a\u00020\u0011J#\u0010\u00d2\u0001\u001a\u00030\u0087\u00012\n\u0010\u0089\u0001\u001a\u0005\u0018\u00010\u008a\u00012\u0007\u0010\u008c\u0001\u001a\u00020\u000f\u00a2\u0006\u0003\u0010\u00d3\u0001J\u0011\u0010\u00d4\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u00d5\u0001\u001a\u00020\nJ\u001a\u0010\u00d6\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u00a0\u0001\u001a\u00020D2\u0007\u0010\u00d7\u0001\u001a\u00020\u0011J\u0011\u0010\u00d8\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u0091\u0001\u001a\u00020\nJ\u0011\u0010\u00d9\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u00da\u0001\u001a\u00020\nJ\u0011\u0010\u00db\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u008c\u0001\u001a\u00020\u000fJ\u0011\u0010\u00dc\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u00d1\u0001\u001a\u00020\u0011J\u0011\u0010\u00dd\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u00de\u0001\u001a\u00020\u0013J\u0011\u0010\u00df\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u00d1\u0001\u001a\u00020\u0011J\b\u0010\u00e0\u0001\u001a\u00030\u0087\u0001J:\u0010\u00e1\u0001\u001a\u00030\u0087\u00012\b\u0010\u00a9\u0001\u001a\u00030\u00aa\u00012\u000f\u0010\u00ab\u0001\u001a\n\u0012\u0005\u0012\u00030\u0087\u00010\u008f\u00012\u0015\u0010\u00ad\u0001\u001a\u0010\u0012\u0004\u0012\u00020\n\u0012\u0005\u0012\u00030\u0087\u00010\u009d\u0001R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00110\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00180\u00170\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u000e\u0010\u001b\u001a\u00020\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u001d\u001a\u00020\u00118F\u00a2\u0006\u0006\u001a\u0004\b\u001e\u0010\u001fR\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00070!\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R/\u0010&\u001a\u0004\u0018\u00010%2\b\u0010$\u001a\u0004\u0018\u00010%8F@FX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b+\u0010,\u001a\u0004\b\'\u0010(\"\u0004\b)\u0010*R\u001d\u0010-\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020.0\u00170\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u0010\u001aR\u001d\u00100\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002010\u00170\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010\u001aR+\u00103\u001a\u00020\u00132\u0006\u0010$\u001a\u00020\u00138F@FX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b8\u0010,\u001a\u0004\b4\u00105\"\u0004\b6\u00107R\u0017\u00109\u001a\b\u0012\u0004\u0012\u00020\n0\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u0010\u001aR\u0017\u0010;\u001a\b\u0012\u0004\u0012\u00020\n0\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u0010\u001aR\u0011\u0010=\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b>\u0010?R\u001d\u0010@\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020A0\u00170\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\bB\u0010\u001aR\u001d\u0010C\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020D0\u00170\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\bE\u0010\u001aR/\u0010F\u001a\u0004\u0018\u00010%2\b\u0010$\u001a\u0004\u0018\u00010%8F@FX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\bI\u0010,\u001a\u0004\bG\u0010(\"\u0004\bH\u0010*R\u0017\u0010J\u001a\b\u0012\u0004\u0012\u00020\n0\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\bK\u0010\u001aR+\u0010L\u001a\u00020\u00112\u0006\u0010$\u001a\u00020\u00118F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\bP\u0010,\u001a\u0004\bM\u0010\u001f\"\u0004\bN\u0010OR/\u0010Q\u001a\u0004\u0018\u00010%2\b\u0010$\u001a\u0004\u0018\u00010%8F@FX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\bT\u0010,\u001a\u0004\bR\u0010(\"\u0004\bS\u0010*R/\u0010U\u001a\u0004\u0018\u00010%2\b\u0010$\u001a\u0004\u0018\u00010%8F@FX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\bX\u0010,\u001a\u0004\bV\u0010(\"\u0004\bW\u0010*R\u0011\u0010Y\u001a\u00020\u00118F\u00a2\u0006\u0006\u001a\u0004\bY\u0010\u001fR\u0011\u0010Z\u001a\u00020\u00118F\u00a2\u0006\u0006\u001a\u0004\bZ\u0010\u001fR\u0011\u0010[\u001a\u00020\u00118F\u00a2\u0006\u0006\u001a\u0004\b[\u0010\u001fR\u0017\u0010\\\u001a\b\u0012\u0004\u0012\u00020\n0\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b]\u0010\u001aR\u0010\u0010^\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010_\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R/\u0010`\u001a\u0004\u0018\u00010%2\b\u0010$\u001a\u0004\u0018\u00010%8F@FX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\bc\u0010,\u001a\u0004\ba\u0010(\"\u0004\bb\u0010*R#\u0010d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00180\u00170\u0016\u00a2\u0006\u000e\n\u0000\u0012\u0004\be\u0010f\u001a\u0004\bg\u0010\u001aR#\u0010h\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020i0\u00170\u0016\u00a2\u0006\u000e\n\u0000\u0012\u0004\bj\u0010f\u001a\u0004\bk\u0010\u001aR\u001d\u0010l\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0016\u00a2\u0006\u000e\n\u0000\u0012\u0004\bm\u0010f\u001a\u0004\bn\u0010\u001aR\u0017\u0010o\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\bp\u0010\u001aR\u0011\u0010q\u001a\u00020\n8F\u00a2\u0006\u0006\u001a\u0004\br\u0010?R\u0011\u0010s\u001a\u00020\u00138F\u00a2\u0006\u0006\u001a\u0004\bt\u00105R\u001d\u0010u\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020v0\u00170\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\bw\u0010\u001aR\u0017\u0010x\u001a\b\u0012\u0004\u0012\u00020\u00110\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\by\u0010\u001aR\u0017\u0010z\u001a\b\u0012\u0004\u0012\u00020\u00130\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b{\u0010\u001aR\u000e\u0010|\u001a\u00020}X\u0082\u0004\u00a2\u0006\u0002\n\u0000R-\u0010~\u001a\u00020\u00112\u0006\u0010$\u001a\u00020\u00118F@FX\u0086\u008e\u0002\u00a2\u0006\u0014\n\u0005\b\u0081\u0001\u0010,\u001a\u0004\b\u007f\u0010\u001f\"\u0005\b\u0080\u0001\u0010OR\u0010\u0010\u0082\u0001\u001a\u00030\u0083\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0084\u0001\u001a\b\u0012\u0004\u0012\u00020\u00110\u0016\u00a2\u0006\t\n\u0000\u001a\u0005\b\u0085\u0001\u0010\u001a\u00a8\u0006\u00e3\u0001"}, d2 = {"Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_budgetAlertEvent", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/expensetracker/app/viewmodel/ExpenseViewModel$BudgetAlertLevel;", "_currencySymbol", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_currentMonthKey", "_displayName", "_languagePref", "_monthlySalary", "", "_reminderEnabled", "", "_reminderHour", "", "_smsDetectionEnabled", "allExpenses", "Lkotlinx/coroutines/flow/StateFlow;", "", "Lcom/expensetracker/app/data/ExpenseEntity;", "getAllExpenses", "()Lkotlinx/coroutines/flow/StateFlow;", "app", "Lcom/expensetracker/app/ExpenseApp;", "biometricEnabled", "getBiometricEnabled", "()Z", "budgetAlertEvent", "Lkotlinx/coroutines/flow/SharedFlow;", "getBudgetAlertEvent", "()Lkotlinx/coroutines/flow/SharedFlow;", "<set-?>", "Landroidx/compose/ui/geometry/Rect;", "budgetCardBounds", "getBudgetCardBounds", "()Landroidx/compose/ui/geometry/Rect;", "setBudgetCardBounds", "(Landroidx/compose/ui/geometry/Rect;)V", "budgetCardBounds$delegate", "Landroidx/compose/runtime/MutableState;", "budgets", "Lcom/expensetracker/app/data/BudgetEntity;", "getBudgets", "categories", "Lcom/expensetracker/app/data/CategoryEntity;", "getCategories", "coachmarkStep", "getCoachmarkStep", "()I", "setCoachmarkStep", "(I)V", "coachmarkStep$delegate", "currencySymbol", "getCurrencySymbol", "currentMonthKey", "getCurrentMonthKey", "customerId", "getCustomerId", "()Ljava/lang/String;", "debtPayments", "Lcom/expensetracker/app/data/DebtPaymentEntity;", "getDebtPayments", "debts", "Lcom/expensetracker/app/data/DebtEntity;", "getDebts", "debtsNavBounds", "getDebtsNavBounds", "setDebtsNavBounds", "debtsNavBounds$delegate", "displayName", "getDisplayName", "driveBackupLoading", "getDriveBackupLoading", "setDriveBackupLoading", "(Z)V", "driveBackupLoading$delegate", "fabBounds", "getFabBounds", "setFabBounds", "fabBounds$delegate", "incomeTilesBounds", "getIncomeTilesBounds", "setIncomeTilesBounds", "incomeTilesBounds$delegate", "isCoachmarksSeen", "isCurrencySetupDone", "isOnboardingDone", "languagePref", "getLanguagePref", "lastAlertLevel", "lastAlertMonthKey", "ledgerNavBounds", "getLedgerNavBounds", "setLedgerNavBounds", "ledgerNavBounds$delegate", "monthExpenses", "getMonthExpenses$annotations", "()V", "getMonthExpenses", "monthIncomeEntries", "Lcom/expensetracker/app/data/IncomeEntity;", "getMonthIncomeEntries$annotations", "getMonthIncomeEntries", "monthIncomeTotal", "getMonthIncomeTotal$annotations", "getMonthIncomeTotal", "monthlySalary", "getMonthlySalary", "onboardingPendingName", "getOnboardingPendingName", "onboardingResumeStep", "getOnboardingResumeStep", "pendingSmsExpenses", "Lcom/expensetracker/app/data/PendingSmsExpense;", "getPendingSmsExpenses", "reminderEnabled", "getReminderEnabled", "reminderHour", "getReminderHour", "repository", "Lcom/expensetracker/app/data/ExpenseRepository;", "screenshotMode", "getScreenshotMode", "setScreenshotMode", "screenshotMode$delegate", "settings", "Lcom/expensetracker/app/data/SettingsRepository;", "smsDetectionEnabled", "getSmsDetectionEnabled", "acceptPendingSms", "", "item", "categoryId", "", "description", "amount", "date", "onDone", "Lkotlin/Function0;", "addCategory", "name", "colorHex", "addIncome", "source", "note", "isRecurring", "convertCurrency", "newSymbol", "rate", "deleteCategory", "category", "onResult", "Lkotlin/Function1;", "Lcom/expensetracker/app/data/DeleteCategoryResult;", "deleteDebt", "debt", "deleteDebtPayment", "payment", "deleteExpense", "expense", "deleteIncome", "income", "dismissPendingSms", "downloadFromDrive", "account", "Lcom/google/android/gms/auth/api/signin/GoogleSignInAccount;", "onSuccess", "Lcom/expensetracker/app/util/BackupManager$ImportResult;", "onError", "exportBackup", "onUri", "Landroid/net/Uri;", "handleIncomingSms", "message", "importBackup", "uri", "isCategoryInUse", "markCoachmarksSeen", "markCurrencySetupDone", "markOnboardingDone", "navigateMonth", "delta", "recolorCategory", "recordDebtPayment", "refreshWidget", "renameCategory", "newName", "resetAllData", "resetOnboardingResume", "saveDebt", "id", "direction", "principal", "interestRatePercent", "minimumPayment", "startDate", "notes", "loanType", "(Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;DDDLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function0;)V", "saveExpense", "(Ljava/lang/Long;JLjava/lang/String;DLjava/lang/String;ZLkotlin/jvm/functions/Function0;)V", "saveOnboardingProgress", "languageCode", "setBiometricEnabled", "enabled", "setBudget", "(Ljava/lang/Long;D)V", "setCurrencySymbol", "symbol", "setDebtClosed", "isClosed", "setDisplayName", "setLanguagePref", "pref", "setMonthlySalary", "setReminderEnabled", "setReminderHour", "hour", "setSmsDetectionEnabled", "toggleScreenshotMode", "uploadToDrive", "BudgetAlertLevel", "app_release"})
public final class ExpenseViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.expensetracker.app.ExpenseApp app = null;
    @org.jetbrains.annotations.NotNull()
    private final com.expensetracker.app.data.ExpenseRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.expensetracker.app.data.SettingsRepository settings = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _currentMonthKey = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> currentMonthKey = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.CategoryEntity>> categories = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> allExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> monthExpenses = null;
    
    /**
     * Current tour step. Int.MAX_VALUE = tour completed / dismissed.
     */
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState coachmarkStep$delegate = null;
    
    /**
     * Bounds of the FAB (+) button — spotlight for step 1.
     */
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState fabBounds$delegate = null;
    
    /**
     * Bounds of the BudgetRingCard — used to derive a smaller ring spotlight for step 2.
     */
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState budgetCardBounds$delegate = null;
    
    /**
     * Bounds of the IncomeBudgetTiles row — spotlight for step 3.
     */
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState incomeTilesBounds$delegate = null;
    
    /**
     * Bounds of the Debts bottom-nav tab — spotlight for step 4.
     */
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState debtsNavBounds$delegate = null;
    
    /**
     * Bounds of the Ledger (Khata) bottom-nav tab — spotlight for step 5.
     */
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState ledgerNavBounds$delegate = null;
    
    /**
     * When true the banner ad is hidden so Play Store screenshots look clean.
     * Toggle via long-press on the version label in Settings.
     */
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState screenshotMode$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _currencySymbol = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> currencySymbol = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _languagePref = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> languagePref = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _displayName = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> displayName = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _smsDetectionEnabled = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> smsDetectionEnabled = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _reminderEnabled = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> reminderEnabled = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _reminderHour = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> reminderHour = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Double> _monthlySalary = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Double> monthlySalary = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.PendingSmsExpense>> pendingSmsExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.BudgetEntity>> budgets = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.DebtEntity>> debts = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.DebtPaymentEntity>> debtPayments = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String customerId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.expensetracker.app.viewmodel.ExpenseViewModel.BudgetAlertLevel> _budgetAlertEvent = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<com.expensetracker.app.viewmodel.ExpenseViewModel.BudgetAlertLevel> budgetAlertEvent = null;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String lastAlertMonthKey = "";
    @org.jetbrains.annotations.Nullable()
    private com.expensetracker.app.viewmodel.ExpenseViewModel.BudgetAlertLevel lastAlertLevel;
    
    /**
     * True while a Drive upload or download is in progress.
     */
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState driveBackupLoading$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.IncomeEntity>> monthIncomeEntries = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Double> monthIncomeTotal = null;
    
    public ExpenseViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getCurrentMonthKey() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.CategoryEntity>> getCategories() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> getAllExpenses() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> getMonthExpenses() {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {kotlinx.coroutines.ExperimentalCoroutinesApi.class})
    @java.lang.Deprecated()
    public static void getMonthExpenses$annotations() {
    }
    
    /**
     * Refresh the home-screen widget after any data mutation. Fire-and-forget.
     */
    private final void refreshWidget() {
    }
    
    public final boolean isCurrencySetupDone() {
        return false;
    }
    
    public final void markCurrencySetupDone() {
    }
    
    public final boolean isOnboardingDone() {
        return false;
    }
    
    public final void markOnboardingDone() {
    }
    
    public final boolean isCoachmarksSeen() {
        return false;
    }
    
    public final void markCoachmarksSeen() {
    }
    
    /**
     * Current tour step. Int.MAX_VALUE = tour completed / dismissed.
     */
    public final int getCoachmarkStep() {
        return 0;
    }
    
    /**
     * Current tour step. Int.MAX_VALUE = tour completed / dismissed.
     */
    public final void setCoachmarkStep(int p0) {
    }
    
    /**
     * Bounds of the FAB (+) button — spotlight for step 1.
     */
    @org.jetbrains.annotations.Nullable()
    public final androidx.compose.ui.geometry.Rect getFabBounds() {
        return null;
    }
    
    /**
     * Bounds of the FAB (+) button — spotlight for step 1.
     */
    public final void setFabBounds(@org.jetbrains.annotations.Nullable()
    androidx.compose.ui.geometry.Rect p0) {
    }
    
    /**
     * Bounds of the BudgetRingCard — used to derive a smaller ring spotlight for step 2.
     */
    @org.jetbrains.annotations.Nullable()
    public final androidx.compose.ui.geometry.Rect getBudgetCardBounds() {
        return null;
    }
    
    /**
     * Bounds of the BudgetRingCard — used to derive a smaller ring spotlight for step 2.
     */
    public final void setBudgetCardBounds(@org.jetbrains.annotations.Nullable()
    androidx.compose.ui.geometry.Rect p0) {
    }
    
    /**
     * Bounds of the IncomeBudgetTiles row — spotlight for step 3.
     */
    @org.jetbrains.annotations.Nullable()
    public final androidx.compose.ui.geometry.Rect getIncomeTilesBounds() {
        return null;
    }
    
    /**
     * Bounds of the IncomeBudgetTiles row — spotlight for step 3.
     */
    public final void setIncomeTilesBounds(@org.jetbrains.annotations.Nullable()
    androidx.compose.ui.geometry.Rect p0) {
    }
    
    /**
     * Bounds of the Debts bottom-nav tab — spotlight for step 4.
     */
    @org.jetbrains.annotations.Nullable()
    public final androidx.compose.ui.geometry.Rect getDebtsNavBounds() {
        return null;
    }
    
    /**
     * Bounds of the Debts bottom-nav tab — spotlight for step 4.
     */
    public final void setDebtsNavBounds(@org.jetbrains.annotations.Nullable()
    androidx.compose.ui.geometry.Rect p0) {
    }
    
    /**
     * Bounds of the Ledger (Khata) bottom-nav tab — spotlight for step 5.
     */
    @org.jetbrains.annotations.Nullable()
    public final androidx.compose.ui.geometry.Rect getLedgerNavBounds() {
        return null;
    }
    
    /**
     * Bounds of the Ledger (Khata) bottom-nav tab — spotlight for step 5.
     */
    public final void setLedgerNavBounds(@org.jetbrains.annotations.Nullable()
    androidx.compose.ui.geometry.Rect p0) {
    }
    
    /**
     * When true the banner ad is hidden so Play Store screenshots look clean.
     * Toggle via long-press on the version label in Settings.
     */
    public final boolean getScreenshotMode() {
        return false;
    }
    
    /**
     * When true the banner ad is hidden so Play Store screenshots look clean.
     * Toggle via long-press on the version label in Settings.
     */
    public final void setScreenshotMode(boolean p0) {
    }
    
    public final void toggleScreenshotMode() {
    }
    
    public final int getOnboardingResumeStep() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOnboardingPendingName() {
        return null;
    }
    
    /**
     * Called when the user taps Continue on Step 1 (language picker).
     * Saves the name + language to prefs and sets the resume step to 2 so that after the
     * Activity recreates due to the locale change the wizard reopens at Step 2 (currency).
     */
    public final void saveOnboardingProgress(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String languageCode) {
    }
    
    /**
     * Clears the mid-wizard resume state once onboarding completes or is skipped.
     */
    public final void resetOnboardingResume() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getCurrencySymbol() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getLanguagePref() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getDisplayName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getSmsDetectionEnabled() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getReminderEnabled() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> getReminderHour() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Double> getMonthlySalary() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.PendingSmsExpense>> getPendingSmsExpenses() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.BudgetEntity>> getBudgets() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.DebtEntity>> getDebts() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.DebtPaymentEntity>> getDebtPayments() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCustomerId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<com.expensetracker.app.viewmodel.ExpenseViewModel.BudgetAlertLevel> getBudgetAlertEvent() {
        return null;
    }
    
    public final void navigateMonth(long delta) {
    }
    
    public final void saveExpense(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, long categoryId, @org.jetbrains.annotations.NotNull()
    java.lang.String description, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String date, boolean isRecurring, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void deleteExpense(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.ExpenseEntity expense, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void addCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String colorHex, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void renameCategory(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.CategoryEntity category, @org.jetbrains.annotations.NotNull()
    java.lang.String newName) {
    }
    
    public final void recolorCategory(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.CategoryEntity category, @org.jetbrains.annotations.NotNull()
    java.lang.String colorHex) {
    }
    
    public final void isCategoryInUse(long categoryId, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onResult) {
    }
    
    public final void deleteCategory(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.CategoryEntity category, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.expensetracker.app.data.DeleteCategoryResult, kotlin.Unit> onResult) {
    }
    
    /**
     * Sets, updates, or (passing 0 or less) clears the standing budget target for
     * [categoryId] — null means the overall monthly budget rather than a per-category one.
     */
    public final void setBudget(@org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, double amount) {
    }
    
    public final void setLanguagePref(@org.jetbrains.annotations.NotNull()
    java.lang.String pref) {
    }
    
    public final void setDisplayName(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
    }
    
    public final void setCurrencySymbol(@org.jetbrains.annotations.NotNull()
    java.lang.String symbol) {
    }
    
    /**
     * Switches currency and rescales every stored expense by [rate] (1 old-currency unit
     * = [rate] new-currency units). All arithmetic is local — no network call.
     */
    public final void convertCurrency(@org.jetbrains.annotations.NotNull()
    java.lang.String newSymbol, double rate, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void resetAllData(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void setSmsDetectionEnabled(boolean enabled) {
    }
    
    public final void setReminderEnabled(boolean enabled) {
    }
    
    public final void setReminderHour(int hour) {
    }
    
    public final void setMonthlySalary(double amount) {
    }
    
    public final boolean getBiometricEnabled() {
        return false;
    }
    
    public final void setBiometricEnabled(boolean enabled) {
    }
    
    public final void exportBackup(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super android.net.Uri, kotlin.Unit> onUri, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError) {
    }
    
    public final void importBackup(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.expensetracker.app.util.BackupManager.ImportResult, kotlin.Unit> onSuccess, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError) {
    }
    
    /**
     * True while a Drive upload or download is in progress.
     */
    public final boolean getDriveBackupLoading() {
        return false;
    }
    
    /**
     * True while a Drive upload or download is in progress.
     */
    private final void setDriveBackupLoading(boolean p0) {
    }
    
    /**
     * Serialises the local database to JSON and uploads it to the signed-in
     * account's private Drive appDataFolder. Network I/O runs on [Dispatchers.IO];
     * callbacks are delivered back on the Main thread.
     */
    public final void uploadToDrive(@org.jetbrains.annotations.NotNull()
    com.google.android.gms.auth.api.signin.GoogleSignInAccount account, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError) {
    }
    
    /**
     * Downloads the backup JSON from Drive and imports it into the local database.
     * Throws (via [onError]) with the special message "no_backup" if no backup
     * file exists yet in Drive.
     */
    public final void downloadFromDrive(@org.jetbrains.annotations.NotNull()
    com.google.android.gms.auth.api.signin.GoogleSignInAccount account, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.expensetracker.app.util.BackupManager.ImportResult, kotlin.Unit> onSuccess, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError) {
    }
    
    /**
     * Called with the raw text of an SMS the user just approved via the system consent prompt.
     * Silently does nothing if the message doesn't look like a debit transaction, or if SMS
     * detection has since been turned off — nothing is ever saved as an expense here, it only
     * lands in the review queue.
     */
    public final void handleIncomingSms(@org.jetbrains.annotations.NotNull()
    java.lang.String message) {
    }
    
    public final void dismissPendingSms(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.PendingSmsExpense item) {
    }
    
    /**
     * User reviewed (and possibly edited) a pending SMS item and confirmed it as a real expense.
     */
    public final void acceptPendingSms(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.PendingSmsExpense item, long categoryId, @org.jetbrains.annotations.NotNull()
    java.lang.String description, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void saveDebt(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String direction, double principal, double interestRatePercent, double minimumPayment, @org.jetbrains.annotations.NotNull()
    java.lang.String startDate, @org.jetbrains.annotations.Nullable()
    java.lang.String notes, @org.jetbrains.annotations.Nullable()
    java.lang.String loanType, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void deleteDebt(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtEntity debt, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void setDebtClosed(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtEntity debt, boolean isClosed) {
    }
    
    public final void recordDebtPayment(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtEntity debt, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.Nullable()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void deleteDebtPayment(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtPaymentEntity payment) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.IncomeEntity>> getMonthIncomeEntries() {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {kotlinx.coroutines.ExperimentalCoroutinesApi.class})
    @java.lang.Deprecated()
    public static void getMonthIncomeEntries$annotations() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Double> getMonthIncomeTotal() {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {kotlinx.coroutines.ExperimentalCoroutinesApi.class})
    @java.lang.Deprecated()
    public static void getMonthIncomeTotal$annotations() {
    }
    
    public final void addIncome(double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String source, @org.jetbrains.annotations.NotNull()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    java.lang.String date, boolean isRecurring, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void deleteIncome(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.IncomeEntity income) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0004\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/expensetracker/app/viewmodel/ExpenseViewModel$BudgetAlertLevel;", "", "(Ljava/lang/String;I)V", "WARNING", "EXCEEDED", "app_release"})
    public static enum BudgetAlertLevel {
        /*public static final*/ WARNING /* = new WARNING() */,
        /*public static final*/ EXCEEDED /* = new EXCEEDED() */;
        
        BudgetAlertLevel() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.expensetracker.app.viewmodel.ExpenseViewModel.BudgetAlertLevel> getEntries() {
            return null;
        }
    }
}