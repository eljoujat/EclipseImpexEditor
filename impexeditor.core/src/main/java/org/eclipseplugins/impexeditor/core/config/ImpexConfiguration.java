/*******************************************************************************
 * Copyright 2014 Youssef EL JAOUJAT.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.eclipseplugins.impexeditor.core.config;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipseplugins.impexeditor.core.Activator;
import org.eclipseplugins.impexeditor.core.editor.ColorManager;
import org.eclipseplugins.impexeditor.core.editor.ImpexColorConstants;
import org.eclipseplugins.impexeditor.core.editor.ImpexPartitionScanner;
import org.eclipseplugins.impexeditor.core.editor.ImpexScanner;
import org.eclipseplugins.impexeditor.core.editor.NonRuleBasedDamagerRepairer;
import org.eclipseplugins.impexeditor.core.editor.autocompletion.ImpexCompletionProposalComputer;
import org.eclipseplugins.impexeditor.core.editor.preferences.PreferenceConstants;


public class ImpexConfiguration extends TextSourceViewerConfiguration
{
	private ImpexScanner scanner;
	private final ColorManager colorManager;
	private ImpexDataDefinition impexDataDeffinition;
	IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	public ImpexConfiguration(final ColorManager colorManager)
	{
		this.colorManager = colorManager;
	}

	public ImpexConfiguration(final ColorManager colorManager,
			final ImpexDataDefinition impexDataDeffinition,
			final IPreferenceStore preferenceStore)
	{
		super(preferenceStore);
		this.colorManager = colorManager;
		this.impexDataDeffinition = impexDataDeffinition;

	}

	@Override
	public String[] getConfiguredContentTypes(final ISourceViewer sourceViewer)
	{
		return new String[]
		{
				IDocument.DEFAULT_CONTENT_TYPE,
				ImpexPartitionScanner.IMPEX_COMMENT, };
	}



	@Override
	public IContentAssistant getContentAssistant(final ISourceViewer sourceViewer)
	{

		final ContentAssistant assistant = new ContentAssistant();
		assistant.enableAutoActivation(true);
		assistant.setContentAssistProcessor(new ImpexCompletionProposalComputer(impexDataDeffinition, scanner),
				IDocument.DEFAULT_CONTENT_TYPE);
		return assistant;
	}


	protected ImpexScanner getXMLScanner()
	{
		if (scanner == null)
		{
			scanner = new ImpexScanner(colorManager, impexDataDeffinition);
			scanner.setDefaultReturnToken(
					new Token(
							new TextAttribute(
									colorManager.getColor(ImpexColorConstants.DEFAULT))));
		}
		return scanner;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(final ISourceViewer sourceViewer)
	{
		final PresentationReconciler reconciler = new PresentationReconciler();
		final DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		final NonRuleBasedDamagerRepairer ndr =
				new NonRuleBasedDamagerRepairer(
						new TextAttribute(
								colorManager.getColor(PreferenceConverter.getColor(store, PreferenceConstants.IMPEX_COMMENTS_COLOR))));
		reconciler.setDamager(ndr, ImpexPartitionScanner.IMPEX_COMMENT);
		reconciler.setRepairer(ndr, ImpexPartitionScanner.IMPEX_COMMENT);
		return reconciler;
	}

}
