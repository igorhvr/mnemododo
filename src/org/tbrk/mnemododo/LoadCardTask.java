/*
 * Copyright (C) 2010 Timothy Bourke
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.tbrk.mnemododo;

import android.app.ProgressDialog;
import mnemogogo.mobile.hexcsv.Card;
import android.util.Log;

class LoadCardTask
  extends ProgressTask<Boolean, Pair<Boolean, String>>
{
    Card card;
    boolean is_question;
    boolean start_thinking;

    boolean center;
    String html_pre;
    String html_post;

    LoadCardTask(TaskListener<Pair<Boolean, String>> callback, Card card,
                 boolean center, String html_pre, String html_post)
    {
        super(callback, R.string.loading_cards);
        this.card = card;

        this.center = center;
        this.html_pre = html_pre;
        this.html_post = html_post;
    }

    public void onPreExecute()
    {
        style = ProgressDialog.STYLE_HORIZONTAL;
    }

    public Pair<Boolean, String> doInBackground(Boolean... options)
    {
        is_question = !options[0];
        if (options.length > 1) {
            start_thinking = options[1];
        } else {
            start_thinking = is_question;
        }

        String html = makeCardHtml(card, !is_question);
        stopOperation();

        return new Pair(start_thinking, html);
    }

    protected void addReplayButton(StringBuffer html, String function)
    {
        html.append("<input type=\"button\" value=\"");
        html.append(getString(R.string.replay_sounds));
        html.append("\" style=\"margin: 1em;\" onclick=\"Mnemododo.");
        html.append(function);
        html.append(";\" />");
    }

    protected String makeCardHtml(Card c, boolean show_answer)
    {
        StringBuffer html = new StringBuffer(html_pre);

        html.append("<script language=\"javascript\">");
        html.append("function scroll() {");
        html.append("  var qelem = document.getElementById('q');");
        html.append("  var aelem = document.getElementById('a');");
        html.append("  if (qelem && aelem) { window.scrollTo(0, qelem.offsetHeight); } }");
        html.append("</script>");

        char[] catname = c.categoryName().toCharArray();
        html.append("<body class=\"");
        for (char l : catname) {
            if (Character.isWhitespace(l)) {
                html.append('_');
            } else if (Character.isUnicodeIdentifierPart(l)) {
                html.append(l);
            }
        }
        html.append("\">");

        String question = c.getQuestion();
        String answer = c.getAnswer();
        
        boolean question_replay = c.hasQuestionSounds();
        boolean answer_replay = c.hasAnswerSounds();
        
        if (center) {
            html.append("<div style=\"text-align: center;\">");
        }

        if (question == null || answer == null) {
            html.append(getString(R.string.no_card_loaded_text));

        } else if (show_answer) {
            if (!card.getOverlay()) {
                html.append("<div class=\"card\" id=\"q\">");
                html.append(question);
                html.append("</div>");
                if (question_replay) {
                    this.addReplayButton(html, "replayQuestionSounds()");
                }
                html.append("<hr/>");
            }
            html.append("<div class=\"card\" id=\"a\">");
            html.append(answer);
            html.append("</div>");
            
            if (answer_replay) {
                this.addReplayButton(html, "replayAnswerSounds()");
            }

        } else {
            html.append("<div class=\"card\" id=\"q\">");
            html.append(question);
            html.append("</div>");

            if (question_replay) {
                this.addReplayButton(html, "replayQuestionSounds()");
            }
        }

        if (center) {
            html.append("</div>");
        }

        html.append(html_post);
        return html.toString();
    }

}

