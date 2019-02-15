package id.dealup.dealup.rolesiswa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.facebook.litho.ComponentContext
import com.facebook.litho.LithoView
import com.facebook.litho.sections.SectionContext
import com.facebook.litho.sections.widget.RecyclerCollectionComponent
import com.google.android.material.tabs.TabLayout
import id.dealup.dealup.LoginActivity
import id.dealup.dealup.R
import id.dealup.dealup.libs.Api
import id.dealup.dealup.libs.Api.Companion.getKewajiban
import id.dealup.dealup.libs.Api.Companion.getPengumuman
import id.dealup.dealup.libs.Store
import id.dealup.dealup.rolesiswa.components.KewajibanTab
import id.dealup.dealup.rolesiswa.components.PengumumanTab
import kotlinx.android.synthetic.main.activity_tab.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject


class TabActivity : AppCompatActivity() {
    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private var profile: JSONObject? = null
    private var murid: JSONObject? = null
    private var sekolah: JSONObject? = null

    /**
     * The [androidx.viewpager.widget.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * androidx.fragment.app.FragmentStatePagerAdapter.
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    var selectedTab = "Kewajiban"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(id.dealup.dealup.R.layout.activity_tab)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedTab = tab.text!!.toString()
                container.setCurrentItem(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        renderProfile()
        loadProfile()
    }

    fun loadProfile() {
        val context = this
        doAsync {
            Api.sessionId = profile!!.getString("sessionId")
            val profile = Api.getProfile()
            if (profile != null) {
                Store.save("profile", profile)
                val sekolah = Api.getSekolah(profile.getInt("sekolah_id"))
                if (sekolah != null) {
                    Store.save("sekolah", sekolah)
                }

                val murid = Api.getMurid(profile!!.getInt("murid_id"))
                if (murid != null) {
                    Store.save("murid", murid)
                }
                uiThread {
                    renderProfile()
                }
            } else {
                val intent = LoginActivity.newIntent(context)
                startActivity(intent)
            }
        }
    }

    fun renderProfile() {
        profile = Store.load("profile")
        sekolah = Store.load("sekolah")
        murid = Store.load("murid")
        try {
            Api.sessionId = profile!!.getString("sessionId")
        } catch (e: Exception) {
            val intent = LoginActivity.newIntent(this)
            startActivity(intent)
            return
        }

        if (profile == null || sekolah == null || murid == null) {
            val intent = LoginActivity.newIntent(this)
            startActivity(intent)
            return
        }

        var namaMurid = murid!!.getString("nama_murid")
        if (namaMurid.length > 18)
            namaMurid = namaMurid.substring(0, Math.min(namaMurid.length, 20)) + "…";

        var namaKelas = murid!!.getString("nama_kelas")
        if (namaKelas.length > 8)
            namaKelas = namaKelas.substring(0, Math.min(namaMurid.length, 10)) + "…";

        toolbar.title = "$namaMurid ($namaKelas)"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(id.dealup.dealup.R.menu.menu_tab, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_logout) {
            Store.clear("profile")
            val intent = LoginActivity.Companion.newIntent(this)
            startActivity(intent)
            return true
        } else if (id == R.id.refresh) {
            if (selectedTab == "Kewajiban") {
                val tab: PlaceholderFragment = mSectionsPagerAdapter!!.getItem(0) as PlaceholderFragment
                tab.loadKewajibanTab(this, true)
            } else {
                val tab: PlaceholderFragment = mSectionsPagerAdapter!!.getItem(1) as PlaceholderFragment
                tab.loadPengumumanTab(this, true)
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getItemPosition(fragItem: Any): Int {
            var position = 0
            return if (position >= 0) position else PagerAdapter.POSITION_NONE
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, TabActivity::class.java)
            return intent
        }
    }

    class PlaceholderFragment : Fragment() {
        fun loadPengumumanTab(context: Context, shouldToast: Boolean = false) {
            pengumumanTab =
                RecyclerCollectionComponent
                    .create(ComponentContext(context))
                    .disablePTR(true)
                    .section(
                        PengumumanTab
                            .create(SectionContext(context))
                            .json("[]")
                            .loading(true)
                            .build()
                    )
                    .build();
            pengumumanComponent.setComponent(pengumumanTab)

            doAsync {
                val murid = Store.load("murid")
                val sekolah = Store.load("sekolah")
                val pengumuman = getPengumuman(
                    sekolah!!.getInt("id")
                )

                pengumumanTab =
                    RecyclerCollectionComponent
                        .create(ComponentContext(context))
                        .disablePTR(true)
                        .section(
                            PengumumanTab
                                .create(SectionContext(context))
                                .json(pengumuman.toString())
                                .loading(false)
                                .build()
                        )
                        .build();
                pengumumanComponent.setComponent(pengumumanTab)
                uiThread {
                    if (shouldToast) {
                        Toast.makeText(context, "Pengumuman berhasil di-refresh", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fun loadKewajibanTab(context: Context, shouldToast: Boolean = false) {
            kewajibanTab =
                RecyclerCollectionComponent
                    .create(ComponentContext(context))
                    .disablePTR(true)
                    .section(
                        KewajibanTab
                            .create(SectionContext(context))
                            .json("[]")
                            .loading(true)
                            .build()
                    )
                    .build();
            kewajibanComponent.setComponent(kewajibanTab)

            doAsync {
                val murid = Store.load("murid")
                val sekolah = Store.load("sekolah")
                val kewajiban = getKewajiban(
                    murid!!.getInt("id"),
                    sekolah!!.getInt("id")
                )

                kewajibanTab =
                    RecyclerCollectionComponent
                        .create(ComponentContext(context))
                        .disablePTR(true)
                        .section(
                            KewajibanTab
                                .create(SectionContext(context))
                                .json(kewajiban.toString())
                                .loading(false)
                                .build()
                        )
                        .build();
                kewajibanComponent.setComponent(kewajibanTab)
                uiThread {
                    if (shouldToast) {
                        Toast.makeText(context, "Kewajiban berhasil di-refresh", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val tab =
                arguments?.getInt(ARG_SECTION_NUMBER)

            if (tab == 1) {
                // TAB: kewajiban
                kewajibanTab =
                    RecyclerCollectionComponent
                        .create(ComponentContext(context))
                        .disablePTR(true)
                        .section(
                            KewajibanTab
                                .create(SectionContext(context))
                                .json("[]")
                                .loading(true)
                                .build()
                        )
                        .build();

                kewajibanComponent = LithoView.create(
                    context,
                    kewajibanTab
                )

                loadKewajibanTab(context!!)

                return kewajibanComponent
            } else {
                // TAB: pengumuman

                pengumumanTab =
                    RecyclerCollectionComponent
                        .create(ComponentContext(context))
                        .disablePTR(true)
                        .section(
                            KewajibanTab
                                .create(SectionContext(context))
                                .json("[]")
                                .loading(true)
                                .build()
                        )
                        .build();

                pengumumanComponent = LithoView.create(
                    context,
                    pengumumanTab
                )

                loadPengumumanTab(context!!)

                return pengumumanComponent
            }
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"
            private lateinit var kewajibanTab: RecyclerCollectionComponent
            private lateinit var kewajibanComponent: LithoView

            private lateinit var pengumumanTab: RecyclerCollectionComponent
            private lateinit var pengumumanComponent: LithoView
            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
