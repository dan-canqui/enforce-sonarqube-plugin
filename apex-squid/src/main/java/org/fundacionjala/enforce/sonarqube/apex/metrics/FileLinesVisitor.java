/*
 * The MIT License
 *
 * Copyright 2016 Fundacion Jala.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fundacionjala.enforce.sonarqube.apex.metrics;

import com.google.common.collect.Sets;
import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import java.util.List;
import java.util.Set;
import org.fundacionjala.enforce.sonarqube.apex.api.ApexMetric;
import org.fundacionjala.enforce.sonarqube.apex.api.ApexTokenType;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.squidbridge.SquidAstVisitor;

/**
 *
 * @author Dan Joel Canqui Aviles
 */
public class FileLinesVisitor extends SquidAstVisitor<Grammar> implements AstAndTokenVisitor {

    private final FileLinesContextFactory fileLinesContextFactory;

    private final Set<Integer> linesOfCode = Sets.newHashSet();

    private final Set<Integer> linesOfComments = Sets.newHashSet();

    private final FileSystem fileSystem;

    public FileLinesVisitor(FileLinesContextFactory fileLinesContextFactory, FileSystem fileSystem) {
        this.fileLinesContextFactory = fileLinesContextFactory;
        this.fileSystem = fileSystem;
    }

    @Override
    public void visitToken(Token token) {
        if (token.getType().equals(GenericTokenType.EOF)) {
            return;
        }

        if (!token.getType().equals(ApexTokenType.DEDENT) && !token.getType().equals(ApexTokenType.INDENT) && !token.getType().equals(ApexTokenType.NEW_LINE)) {
            /* Handle all the lines of the token */
            String[] tokenLines = token.getValue().split("\n", -1);
            for (int line = token.getLine(); line < token.getLine() + tokenLines.length; line++) {
                linesOfCode.add(line);
            }
        }

        List<Trivia> trivias = token.getTrivia();
        for (Trivia trivia : trivias) {
            if (trivia.isComment()) {
                linesOfComments.add(trivia.getToken().getLine());
            }
        }
    }

    @Override
    public void leaveFile(AstNode astNode) {
        InputFile inputFile = fileSystem.inputFile(fileSystem.predicates().is(getContext().getFile()));
        if (inputFile == null) {
            throw new IllegalStateException("InputFile is null, but it should not be.");
        }
        FileLinesContext fileLinesContext = fileLinesContextFactory.createFor(inputFile);

        int fileLength = getContext().peekSourceCode().getInt(ApexMetric.LINES);
        for (int line = 1; line <= fileLength; line++) {
            fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, line, linesOfCode.contains(line) ? 1 : 0);
            fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, line, linesOfComments.contains(line) ? 1 : 0);
        }
        fileLinesContext.save();

        linesOfCode.clear();
        linesOfComments.clear();
    }
}
