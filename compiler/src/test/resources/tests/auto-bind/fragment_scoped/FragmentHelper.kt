package se.ansman.fragment_scoped

import dagger.hilt.android.scopes.FragmentScoped
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

interface FragmentHelper

@AutoBind
@FragmentScoped
class RealFragmentHelper @Inject constructor() : FragmentHelper